import bagel.util.Point;

public class RegularTankProjectile extends Projectile {
    private static final String IMAGE_FILE = "res/images/tank_projectile.png";


    /**
     * Instantiates a new Regular tank projectile.
     *
     * @param startingPoint the starting point of projectile (the regular tank it's shooting from).
     * @param targetPoint   the target point (the centre of slicer it's aiming for).
     * @param targetSlicer  the target slicer (the slicer it's aiming for).
     */
    public RegularTankProjectile(Point startingPoint, Point targetPoint, Slicer targetSlicer) {
        super(IMAGE_FILE, startingPoint, targetPoint, targetSlicer);
    }
}
