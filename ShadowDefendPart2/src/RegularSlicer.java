import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.List;

public class RegularSlicer extends Slicer {
    // regular slicer specs
    private static final String IMAGE_FILE = "res/images/slicer.png";
    private static int HEALTH = 1;
    private static int REWARD = 2;
    private static int PENALTY = 1;
    private static double INITIAL_SPEED = 2.0;

    /**
     * Instantiates a new regular slicer.
     *
     * @param polylines     the polylines to traverse
     * @param startingPoint the starting point
     * @param nextIndex     the next index in the polyline.
     */
    public RegularSlicer(List<Point> polylines, Point startingPoint, int nextIndex) {
        super(startingPoint, polylines, IMAGE_FILE, HEALTH, REWARD, PENALTY,
                INITIAL_SPEED, nextIndex);
    }

    // getter
    public static int getHEALTH() {
        return HEALTH;
    }
    public static int getPENALTY() {
        return PENALTY;
    }
    public static double getInitialSpeed() {
        return INITIAL_SPEED;
    }

    /**
     * Overrides abstract method inherited from Slicer parent class.
     * Do nothing as regular slicers do not have children to spawn upon its elimination.
     *
     * @param input Potentially reading from keyboard and mouse input.
     */
    @Override
    public ArrayList<Slicer> spawnChildSlicers(Input input) {
        // do nothing as regular slicers have no child slicer
        return null;
    }


    /**
     * Overrides abstract method inherited from Slicer parent class.
     * Top-level, overall update method that updates this regular slicer until it finishes traversal / is eliminated.
     *
     * @param input Potentially reading from keyboard and mouse input.
     */
    @Override
    public void update(Input input) {
        // check if any explosives will hurt this slicer
        checkExplosivesRange();
        // check health status
        if (this.getHealth() <= 0) {
            spawnChildSlicers(input);
            this.setEliminated(true);
            return;
        }
        // check if this slicer finished traversal
        if (this.isFinished()) {
            return;
        }
        // update traversal if not finished
        // Obtain where we currently are, and where we want to be
        Point currentPoint = getCenter();
        Point targetPoint = this.getPolylines().get(this.getNextPositionIndex());
        // Convert them to vectors to perform some very basic vector math
        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);
        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();
        // Check if we are close to the target point
        if (magnitude < this.getSlicerSpeed() * ShadowDefend.getTimescale()) {
            // Check if we have reached the end
            if (this.getNextPositionIndex() == this.getPolylines().size() - 1) {
                this.setFinished(true);
                return;
            } else {
                // Make our focus the next point in the polyline
                this.setNextPositionIndex(this.getNextPositionIndex()+1);
            }
        }
        // Move towards the target point
        // We do this by getting a unit vector in the direction of our target, and multiplying it
        // by the speed of the slicer (accounting for the timescale)
        super.move(distance.normalised().mul(this.getSlicerSpeed() * ShadowDefend.getTimescale()));
        // Update current rotation angle to face target point
        setAngle(Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
        // render the slicer with super class method if slicer does not intersect with panel
        super.update(input);
    }
}
