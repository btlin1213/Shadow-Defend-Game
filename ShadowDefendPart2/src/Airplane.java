import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Airplane.
 */
public class Airplane extends Tower {
    // default specs
    private static final Image AIRPLANE_IMAGE = new Image("res/images/airsupport.png");
    private final int MIN_RANGE = 1;
    private final int MAX_RANGE = 2;
    private int INITIAL_SPEED = 3;
    private final long TO_MILLI = 1000;
    private final String H = "horizontal";
    private final String V = "vertical";
    private int ORIGIN = 0;
    private static int COST = 500;
    // plane specs
    private Point horizontalStartPoint;
    private Point horizontalEndPoint;
    private Point verticalStartPoint;
    private Point verticalEndPoint;
    private double angle = 0.0;
    // explosive specs
    private long dropDelay;
    private ArrayList<Explosive> explosives;
    private long lastDropTime;
    // flags
    private boolean flightStarted = false;
    private boolean flightFinished = false;
    private boolean allDetonated = false;
    private String direction;


    /**
     * Instantiates a new Airplane.
     *
     * @param position  the position where Airplane is to be rendered upon selection
     * @param towers    the list of all towers currently in-game (to prevent placing Airplane on another tower)
     * @param polylines the polylines (to prevent placing Airplane on polyline)
     * @param map       the map (to prevent placing Airplane on blocked tiles)
     * @param direction the direction that this Airplane should fly in.
     */
    public Airplane(Point position, ArrayList<Tower> towers, List<Point> polylines, TiledMap map, String direction) {
        super(position, AIRPLANE_IMAGE, towers, polylines, map, COST);
        this.direction = direction;
    }

    // getters
    public boolean isAllDetonated() {
        return allDetonated;
    }


    /**
     * Returns a boolean value to indicate whether required delay time since last dropped explosive has elapsed
     *
     * @param dropDelay    required delay time
     * @param lastDropTime time of last dropped explosive
     * @param currentTime  current time
     * @return the boolean
     */
    public boolean dropDelayPassed(long dropDelay, long lastDropTime, long currentTime) {
        return currentTime-lastDropTime >= dropDelay;
    }

    /**
     * Moves the Airplane by a specified delta
     *
     * @param dx The move delta vector
     */
    public void move(Vector2 dx) {
        getTowerRect().moveTo(getTowerRect().topLeft().asVector().add(dx).asPoint());
        setPosition(getTowerRect().centre());
    }

    /**
     * Add new explosive to explosive list every 1-2 seconds.
     */
    public void addExplosives() {
        dropDelay = (long) ((Math.random()*(MAX_RANGE-MIN_RANGE+1))+ MIN_RANGE)*TO_MILLI / ShadowDefend.getTimescale();
        if (dropDelayPassed(dropDelay, lastDropTime, System.currentTimeMillis())) {
            explosives.add(new Explosive(getPosition()));
            lastDropTime = System.currentTimeMillis();
        }
    }
    /**
     * Update the state of explosives list until they are all detonated.
     */
    public void updateExplosives() {
        if (explosives != null) {
            for (int i=explosives.size()-1; i>=0; i--) {
                Explosive explosive = explosives.get(i);
                if (explosive.isDetonated()) {
                    // move onto updating next explosive
                    continue;
                }
                explosive.update();
            }
            //  if all detonated, mark flag
            if (explosives.size()>0) {
                for (int i=0; i<explosives.size(); i++) {
                    if (!explosives.get(i).isDetonated()) {
                        // at least 1 not detonated yet
                        break;
                    }
                    else if (i==explosives.size()-1 && explosives.get(i).isDetonated()) {
                        // even the last one has detonated, mark flag to remove this airplane from game
                        allDetonated = true;
                    }
                }
            }
        }
    }

    /**
     * Overrides abstract method from Tower class. Updates the state of an Airplane after it is placed.
     * A state of placed Airplane is different to the state of a placed Tank as Tank is immobile and Airplane is mobile.
     *
     * @param input Potentially reading from keyboard or mouse input.
     */
    @Override
    public void updatePlaced(Input input) {
        // Obtain where we currently are, and where we want to be
        Point currentPoint = getPosition();
        // set default to fly horizontally
        Point targetPoint = horizontalEndPoint;

        // change end point if flying vertically
        if (direction.equals(V)) {
            targetPoint = verticalEndPoint;
        }

        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);
        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();
        // if still not close enough to target point
        // airplane specs
        int airplaneSpeed = INITIAL_SPEED;
        if (magnitude > airplaneSpeed * ShadowDefend.getTimescale()) {
            // Move towards the target point by getting a unit vector in the direction of our target,
            // and multiplying it by the speed of the slicer (accounting for the timescale)
            move(distance.normalised().mul(airplaneSpeed * ShadowDefend.getTimescale()));
        }

        // Check if we are close to the target point
        else if (magnitude <= airplaneSpeed * ShadowDefend.getTimescale()) {
            // close enough to the end, mark flying flag as finished
            flightFinished = true;
        }

        // render plane
        angle = Math.PI/2 + Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x);
        getTowerImage().draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(angle));
    }


    /**
     * Overrides inherited abstract method as Airplane drops explosives while Tanks shoot projectiles.
     * Top-level overall update method for Airplane, implemented in detail to override abstract method from Tower.
     * Call updateSelection when Airplane is selected from Buy Panel
     * Call updatePlaced when selected Airplane is placed
     *
     * @param input Potentially reading from keyboard or mouse input.
     */
    @Override
    public void update(Input input) {
        // selected but not placed
        if (isSelected() && !isPlaced()) {
            updateSelection(input);
        }
        // placed but before flying
        if (isPlaced() & !flightStarted) {
            // initialise specs
            explosives = new ArrayList<Explosive>();
            lastDropTime = System.currentTimeMillis();
            Point placedPoint = getPosition();
            horizontalStartPoint = new Point(ORIGIN, placedPoint.y);
            horizontalEndPoint = new Point(Window.getWidth(), placedPoint.y);
            verticalStartPoint = new Point(placedPoint.x, ORIGIN);
            verticalEndPoint = new Point(placedPoint.x, Window.getHeight());
            // determine direction
            if (direction.equals(H)) {
                setPosition(horizontalStartPoint);
            }
            else if (direction.equals(V)) {
                setPosition(verticalStartPoint);
            }
            // get bounding box of airplane around its starting position
            setTowerRect(getTowerImage().getBoundingBoxAt(getPosition()));
            // mark flag
            flightStarted = true;
        }
        // illustrate flying airplane and dropping explosives as long as it's flying
        if (flightStarted && !flightFinished) {
            updatePlaced(input);
            addExplosives();
            updateExplosives();
        }
        // if no longer flying then keep updating explosives list but do not drop anymore
        if (flightFinished && !allDetonated) {
            updateExplosives();
        }
    }
}
