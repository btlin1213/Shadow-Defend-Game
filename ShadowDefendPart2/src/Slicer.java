import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class Slicer {
    // image specs
    private Image slicerImage;
    private final Rectangle rect;
    private double angle;
    // slicer specs
    private int health;
    private int reward;
    private int penalty;
    // speed specs
    private double slicerSpeed;
    // location specs
    private List<Point> polylines;
    private int nextPositionIndex;
    // flags
    private boolean finished = false;
    private boolean eliminated = false;
    private Point eliminatedPosition = null;

    /**
     * Constructor for Slicer (abstract).
     *
     * @param startingPoint     the starting point of polylines.
     * @param polylines         the polylines to traverse.
     * @param imageSrc          the image source of this slicer.
     * @param health            the health spec of this slicer.
     * @param reward            the reward spec of this slicer.
     * @param penalty           the penalty spec of this slicer.
     * @param slicerSpeed       the initial slicer speed without influence from timescale of the game.
     * @param nextPositionIndex the index of next position to traverse in polyline.
     */
    public Slicer(Point startingPoint, List<Point> polylines, String imageSrc, int health, int reward, int penalty, double slicerSpeed,
                  int nextPositionIndex) {
        this.slicerImage = new Image(imageSrc);
        this.polylines = polylines;
        this.rect = slicerImage.getBoundingBoxAt(startingPoint);
        this.angle = 0;
        this.health = health;
        this.reward = reward;
        this.penalty = penalty;
        this.slicerSpeed = slicerSpeed;
        this.nextPositionIndex = nextPositionIndex;
    }

    /**
     * Adapted from Rohyl's Project 1 solution - get centre of bounding box of this slicer.
     *
     * @return centre of bounding box of this slicer.
     */
    public Point getCenter() {
        return getRect().centre();
    }

    // getter
    public Rectangle getRect() {
        return rect;
    }
    public boolean isFinished() {
        return finished;
    }
    public List<Point> getPolylines() {
        return polylines;
    }
    public int getNextPositionIndex() {
        return nextPositionIndex;
    }
    public double getSlicerSpeed() {
        return slicerSpeed;
    }
    public int getHealth() {
        return health;
    }
    public int getReward() {
        return reward;
    }
    public int getPenalty() {
        return penalty;
    }
    public boolean isEliminated() {
        return eliminated;
    }
    public Point getEliminatedPosition() {
        return eliminatedPosition;
    }

    // setter
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public void setNextPositionIndex(int nextPositionIndex) {
        this.nextPositionIndex = nextPositionIndex;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
    public void setEliminatedPosition(Point eliminatedPosition) {
        this.eliminatedPosition = eliminatedPosition;
    }


    /**
     * Spawn child slicers array list. To be implemented in detail in each subclass of Slicer.
     *
     * @param input the input from keyboard or/and mouse.
     * @return the arraylist to add spawned child slicers to and combine with the current spawn event.
     */
    public abstract ArrayList<Slicer> spawnChildSlicers(Input input);

    /**
     * Returns a boolean value to indicate whether the required time has passed between given time and now.
     *
     * @param lengthInMS  the length in ms of required time length.
     * @param earlierTime the earlier time.
     * @param laterTime   the later time.
     * @return the boolean value as mentioned above.
     */
    public boolean timePassed(long lengthInMS, long earlierTime, long laterTime) {
        return laterTime-earlierTime >= lengthInMS;
    }


    /**
     * Check if any explosives detonated will hurt this slicer.
     */
    public void checkExplosivesRange() {
        for (HashMap.Entry<Rectangle, Explosive> entry : Level.getExplosivesInUse().entrySet()) {
            // if this slicer intersects with the rectangle of an explosive that just detonated
            if (entry != null && !entry.getValue().isRemoved()) {
                if (entry.getKey().intersects(getRect()) && !timePassed(Explosive.getDetonationMs(),
                        entry.getValue().getEntryTime(), System.currentTimeMillis())) {
                    // deal damage - if health more than damage, subtract it
                    this.setHealth(this.getHealth() - entry.getValue().getDAMAGE());
                }
                if (timePassed(Explosive.getDetonationMs(), entry.getValue().getEntryTime(), System.currentTimeMillis())) {
                    entry.getValue().setRemoved(true);
                }
            }
        }
    }

    /**
     * Moves the Slicer by a specified delta
     *
     * @param dx The move delta vector
     */
    public void move(Vector2 dx) {
        rect.moveTo(rect.topLeft().asVector().add(dx).asPoint());
    }


    /**
     * Updates the Slicer. Default behaviour is to render the Sprite at its current position.
     */
    public void update(Input input) {
        slicerImage.draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(angle));
    }


}

