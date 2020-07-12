import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

public abstract class Projectile {
    // image specs
    private Image projectileImage;
    private final Rectangle projectileRect;
    // projectile specs
    private Point position;
    private Point targetPoint;
    private static final int INITIAL_SPEED = 10;
    private int projectileSpeed = INITIAL_SPEED;
    private Slicer targetSlicer;
    // flags
    private boolean finished = false;

    /**
     * Constructor for a new Projectile (abstract).
     *
     * @param projectileImageSrc the projectile image source.
     * @param startingPoint      the starting point of projectile (the tank it's shooting from).
     * @param targetPoint        the target point (the centre of slicer it's aiming for).
     * @param targetSlicer       the target slicer (the slicer it's aiming for).
     */
    public Projectile(String projectileImageSrc, Point startingPoint, Point targetPoint, Slicer targetSlicer) {
        this.projectileImage = new Image(projectileImageSrc);
        this.projectileRect = projectileImage.getBoundingBoxAt(startingPoint);
        this.position = startingPoint;
        this.targetPoint = targetPoint;
        this.targetSlicer = targetSlicer;
    }

    /**
     * Gets center of projectile's bounding box.
     *
     * @return the center point.
     */
    public Point getCenter() {
        return getProjectileRect().centre();
    }

    // getter
    public Rectangle getProjectileRect() {
        return projectileRect;
    }
    public int getProjectileSpeed() {
        return projectileSpeed;
    }
    public boolean isFinished() {
        return finished;
    }
    public Slicer getTargetSlicer() {
        return targetSlicer;
    }

    // setter
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Moves the projectile by a specified delta
     *
     * @param dx The move delta vector
     */
    public void move(Vector2 dx) {
        projectileRect.moveTo(projectileRect.topLeft().asVector().add(dx).asPoint());
    }


    /**
     * Update the state of projectile until it hits target slicer.
     */
    public void update() {
        if (this.isFinished()) {
            return;
        }
        // Convert them to vectors to perform some very basic vector math
        Vector2 target = targetPoint.asVector();
        Vector2 current = position.asVector();
        Vector2 distance = target.sub(current);
        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();
        // Check if we are close to the target point
        if (magnitude < this.getProjectileSpeed() * ShadowDefend.getTimescale() || this.getProjectileRect().intersects(targetPoint)) {
            // simply mark flag when projectile hits target, damage is dealt in tank class.
            this.setFinished(true);
            return;
        }
        // Move towards the target point
        // We do this by getting a unit vector in the direction of our target, and multiplying it
        // by the speed of the projectile (accounting for the timescale)
        move(distance.normalised().mul(this.getProjectileSpeed() * ShadowDefend.getTimescale()));
        // render at updated point
        projectileImage.draw(getCenter().x, getCenter().y);
    }
}
