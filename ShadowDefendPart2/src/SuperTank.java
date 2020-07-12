import bagel.Image;
import bagel.map.TiledMap;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;


public class SuperTank extends Tank {
    private static final Image SUPERTANK_IMAGE = new Image("res/images/supertank.png");
    private static int EFFECT_RADIUS = 150;
    private static int DAMAGE = 3*RegularTank.getDAMAGE();
    private static int COOLDOWN = 500;
    private static int COST = 600;
    private ArrayList<SuperTankProjectile> superProjectiles = new ArrayList<SuperTankProjectile>();

    /**
     * Instantiates a new super tank.
     *
     * @param position of super tank.
     * @param towers available on the map of current level right now.
     * @param polylines of current level.
     * @param map of current level.
     * @param projectiles list of projectiles of this tank.
     */
    public SuperTank(Point position, ArrayList<Tower> towers, List<Point> polylines, TiledMap map, ArrayList<Projectile> projectiles) {
        super(position, SUPERTANK_IMAGE, EFFECT_RADIUS, DAMAGE, COOLDOWN, towers, polylines, map, projectiles, COST);
    }

}
