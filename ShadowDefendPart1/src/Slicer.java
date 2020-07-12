import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

public class Slicer {
    // image specs
    private static final Image slicerImage = new Image("res/images/slicer.png");
    // location specs
    private Point slicerLocation;
    private double slicerX;
    private double slicerY;
    private int nextPositionIndex;
    // speed specs
    private long spawnTime;
    private double slicerSpeed;
    private double threshold = slicerSpeed;
    // status specs
    private boolean spawned;
    private boolean finished;

    // Constructor
    public Slicer(Point slicerLocation, int polylinePositionIndex, double slicerSpeed, long spawnTime) {
        this.slicerLocation = slicerLocation;
        this.slicerX = slicerLocation.x;
        this.slicerY = slicerLocation.y;
        this.nextPositionIndex = polylinePositionIndex + 1;
        this.slicerSpeed = slicerSpeed;
        this.spawnTime = spawnTime;
    }


    // getter methods
    public Point getSlicerLocation() { return slicerLocation; }

    public int getNextPositionIndex() { return nextPositionIndex; }

    public long getSpawnTime() { return spawnTime; }

    public boolean isSpawned() {
        return spawned;
    }

    public boolean isFinished() {
        return finished;
    }


    // setter methods
    public void setSlicerLocation(Point slicerLocation) {
        this.slicerLocation = slicerLocation;
    }

    public void setSlicerX(double slicerX) {
        this.slicerX = slicerX;
    }

    public void setSlicerY(double slicerY) {
        this.slicerY = slicerY;
    }

    public void setThreshold(double threshold) { this.threshold = threshold; }

    public void setSlicerSpeed(double slicerSpeed) { this.slicerSpeed = slicerSpeed; }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    // render with default angle (from first point to second point in polyline)
    public void render(Point newSlicerLocation) {
        slicerImage.draw(newSlicerLocation.x, newSlicerLocation.y);
    }
    // render with updated angles (for all subsequent points in polyline)
    public void renderWithAngle(Point newSlicerLocation, Point nextPosition) {
        double radian = Math.atan2(nextPosition.y-newSlicerLocation.y, nextPosition.x-newSlicerLocation.x);
        DrawOptions drawOption = new DrawOptions();
        drawOption.setRotation(radian);
        slicerImage.draw(newSlicerLocation.x, newSlicerLocation.y, drawOption);
    }

    // get the vector from current location to next location
    public Vector2 getVectorToNextPosition(Point currentPosition, Point nextPosition) {
        Vector2 currentPositionVector = currentPosition.asVector();
        Vector2 nextPositionVector = nextPosition.asVector();
        return nextPositionVector.sub(currentPositionVector);
    }

    // update this slicer's location
    public void update(Point currentPosition, Point nextPosition) {
        // Render the slicer at its current angle
        this.renderWithAngle(currentPosition, nextPosition);

        // Initialise the vectors required to advance the slicer
        Vector2 currentPositionVector = currentPosition.asVector();
        Vector2 vectorToNextPosition = getVectorToNextPosition(currentPosition, nextPosition);
        Vector2 directionVector = vectorToNextPosition.normalised();
        Vector2 incrementVector = directionVector.mul(slicerSpeed);
        double vectorToNextPositionLength = vectorToNextPosition.length();

        // Keep moving towards next position if the distance has not reached threshold
        if (vectorToNextPositionLength > threshold) {
            currentPositionVector = currentPositionVector.add(incrementVector);

            // Update slicer's current location
            this.setSlicerX(currentPositionVector.x);
            this.setSlicerY(currentPositionVector.y);
            this.setSlicerLocation(currentPositionVector.asPoint());
        }

         // When threshold reached, update slicer location to the location pointed at by nextPosition
         // then increment both currentPosition and nextPosition indexes
        else {
            this.setSlicerX(nextPosition.x);
            this.setSlicerY(nextPosition.y);
            this.setSlicerLocation(nextPosition);
            this.nextPositionIndex += 1;
        }
    }
}
