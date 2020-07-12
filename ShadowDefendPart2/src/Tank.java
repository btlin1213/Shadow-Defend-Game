import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.List;


public abstract class Tank extends Tower {
    private Image tankImage;
    private int effectRadius;
    private int damage;
    private int cooldown;
    private long lastShootTime = 0;
    private ArrayList<Projectile> projectiles;
    private SpawnEvent currSpawnEvent;

    /**
     * Constructor for a new Tank (abstract).
     * Abstract as there are two specific types of tanks with differing specs.
     *
     * @param position     the position to initialise tank at initially.
     * @param tankImage    the tank image.
     * @param effectRadius the effect radius.
     * @param damage       the damage.
     * @param cooldown     the cooldown.
     * @param towers       the towers on the map of this level right now.
     * @param polylines    the polylines of this level.
     * @param map          the map of this level.
     * @param projectiles  the arraylist of projectiles to be updated.
     */
    public Tank(Point position, Image tankImage, int effectRadius, int damage, int cooldown,
                ArrayList<Tower> towers, List<Point> polylines, TiledMap map, ArrayList<Projectile> projectiles, int cost) {
        super(position, tankImage, towers, polylines, map, cost);
        this.tankImage = tankImage;
        this.effectRadius = effectRadius;
        this.damage = damage;
        this.cooldown = cooldown;
        this.projectiles = projectiles;
    }


    /**
     * Find nearest slicer within effect radius to target.
     *
     * @param effectRadius the effect radius.
     * @return the slicer to target.
     */
    public Slicer findNearestSlicer(int effectRadius) {
        if (ShadowDefend.getCurrentWaveEvent() != null) {
            // if current event is spawn event then update currSpawnEvent
            if (ShadowDefend.getCurrentWaveEvent() instanceof SpawnEvent) {
                currSpawnEvent = (SpawnEvent) ShadowDefend.getCurrentWaveEvent();
            }
            // if current event is delay event then do not update currSpawnEvent
            // keep shooting slicers from last spawn event
            if (currSpawnEvent != null) {
                for (Slicer slicer : currSpawnEvent.getSlicers()) {
                    if (getPosition().distanceTo(slicer.getCenter()) <= effectRadius) {
                        return slicer;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Returns a boolean to indicate whether this slicer is within effect radius of this tank location.
     *
     * @param tankLocation   the tank location
     * @param slicerLocation the slicer location
     * @param effectRadius   the effect radius
     * @return the boolean mentioned above.
     */
    public boolean inRadius(Point tankLocation, Point slicerLocation, int effectRadius) {
        return tankLocation.distanceTo(slicerLocation) <= effectRadius;
    }


    /**
     * Returns a boolean to indicate whether the cooldown time has passed for this tank to launch another projectile.
     *
     * @param cooldown      the cooldown time of this tank (influenced by timescale).
     * @param lastShootTime the time when this tank last shot a projectile.
     * @param currentTime   the current time.
     * @return the boolean mentioned above.
     */
    public boolean cooldownPassed(int cooldown, long lastShootTime, long currentTime) {
        return currentTime-lastShootTime >= cooldown/ShadowDefend.getTimescale();
    }


    /**
     * Implement details of abstract method inherited from Tower class.
     * Updating state of a placed tank.
     * A state of placed Tank is different to the state of a placed Airplane as Tank is immobile and Airplane is mobile.
     *
     * @param input the input from mouse / keyboard.
     */
    @Override
    public void updatePlaced(Input input) {
        for (int i=projectiles.size()-1; i>=0; i--) {
            Projectile p = projectiles.get(i);
            p.update();
            if (p.isFinished()) {
                // deal damage - if health is equal or more than damage
                if (p.getTargetSlicer().getHealth() >= damage) {
                    p.getTargetSlicer().setHealth(p.getTargetSlicer().getHealth()-damage);
                }
                // remove from projectiles list
                projectiles.remove(p);
            }
        }

        // shoot nearest slicer if wave in progress and cooldown has passed
        Slicer nearestSlicer = findNearestSlicer(effectRadius);
        if (nearestSlicer != null && cooldownPassed(cooldown, lastShootTime, System.currentTimeMillis())) {
            Point currentPoint = getCenter();
            Point targetPoint = nearestSlicer.getCenter();
            Rectangle targetRect = nearestSlicer.getRect();
            // reset angle to target slicer
            setAngle(Math.PI/2 + Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
            // initialise projectile
            Projectile currProjectile = null;
            if (this.getClass().equals(RegularTank.class)) {
                currProjectile = new RegularTankProjectile(currentPoint, targetPoint, nearestSlicer);
            }
            else if (this.getClass().equals(SuperTank.class)) {
                currProjectile = new SuperTankProjectile(currentPoint, targetPoint, nearestSlicer);
            }

            // as long as slicer still in range, keep attacking it
            if (currProjectile != null && inRadius(currentPoint, nearestSlicer.getCenter(), effectRadius)) {
                lastShootTime = System.currentTimeMillis();
                projectiles.add(currProjectile);
            }
        }
        // render tank in direction of target slicer
        tankImage.draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(this.getAngle()));
    }


    /**
     * Overrides abstract method from Tower class.
     * Implements in detail the top-level, overall update method for Tank.
     *
     * @param input the input from mouse / keyboard.
     */
    @Override
    public void update(Input input) {
        if (isSelected() && !isPlaced()) {
            updateSelection(input);
        }
        if (isPlaced()) {
            updatePlaced(input);
        }
    }
}
