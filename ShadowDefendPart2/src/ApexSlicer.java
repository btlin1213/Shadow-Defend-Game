import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.List;

public class ApexSlicer extends Slicer {
    // apex slicer specs
    private static final String IMAGE_FILE = "res/images/apexslicer.png";
    private static int NEW_MEGA_SLICERS = 4;
    private static int HEALTH = 25*RegularSlicer.getHEALTH();
    private static int REWARD = 150;
    private static int PENALTY = NEW_MEGA_SLICERS * MegaSlicer.getPENALTY();
    private static double INITIAL_SPEED = 0.50 * MegaSlicer.getInitialSpeed();
    private ArrayList<Slicer> childSlicers = new ArrayList<Slicer>();

    /**
     * Instantiates a new apex slicer.
     *
     * @param polylines     the polylines to traverse
     * @param startingPoint the starting point
     * @param nextIndex     the next index in the polyline.
     */
    public ApexSlicer(List<Point> polylines, Point startingPoint, int nextIndex) {
        super(startingPoint, polylines, IMAGE_FILE, HEALTH, REWARD, PENALTY,
                INITIAL_SPEED, nextIndex);
    }


    /**
     * Overrides abstract method inherited from Slicer parent class.
     * Implements detail such as spawning mega slicer children upon elimination.
     *
     * @param input Potentially reading from keyboard and mouse input.
     */
    @Override
    public ArrayList<Slicer> spawnChildSlicers(Input input) {
        for (int i=0; i<NEW_MEGA_SLICERS; i++) {
            if (getEliminatedPosition() != null) {
                MegaSlicer newChildSlicer = new MegaSlicer(getPolylines(), getEliminatedPosition(), getNextPositionIndex());
                childSlicers.add(newChildSlicer);
            }
        }
        return childSlicers;
    }

    /**
     * Overrides abstract method inherited from Slicer parent class.
     * Top-level, overall update method that updates this apex slicer until it finishes traversal / is eliminated.
     *
     * @param input Potentially reading from keyboard and mouse input.
     */
    @Override
    public void update(Input input) {
        // check if any explosives will hurt this slicer
        checkExplosivesRange();
        if (this.getHealth() <= 0) {
            this.setEliminatedPosition(this.getCenter());
            spawnChildSlicers(input);
            this.setEliminated(true);
            return;
        }
        if (this.isFinished()) {
            return;
        }
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
        super.update(input);
    }



}

