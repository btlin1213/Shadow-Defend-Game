import bagel.Image;
import bagel.map.TiledMap;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

public class RegularTank extends Tank {
    private static final Image REGULAR_TANK_IMAGE = new Image("res/images/tank.png");
    private static int EFFECT_RADIUS = 100;
    private static int DAMAGE = 1;
    private static int COOLDOWN = 1000;
    private static int COST = 250;

    /**
     * Instantiates a new regular tank.
     *
     * @param position of regular tank.
     * @param towers available on the map of current level right now.
     * @param polylines of current level.
     * @param map of current level.
     * @param projectiles list of projectiles of this tank.
     */
    public RegularTank(Point position, ArrayList<Tower> towers, List<Point> polylines, TiledMap map, ArrayList<Projectile> projectiles) {
        super(position, REGULAR_TANK_IMAGE, EFFECT_RADIUS, DAMAGE, COOLDOWN, towers, polylines, map, projectiles, COST);
    }


    public static int getDAMAGE() {
        return DAMAGE;
    }
}
