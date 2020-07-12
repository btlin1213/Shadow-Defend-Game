import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Explosive {
    // specs
    private static final Image IMAGE = new Image("res/images/explosive.png");
    private final int EFFECT_RADIUS = 200;
    private final int DETONATION_TIME = 2;
    private final int DAMAGE = 500;
    private final long TO_MILLI = 1000;
    private static final long DETONATION_MS = 1000;
    private Point position;
    private long dropTime;
    private long entryTime;
    private Rectangle effectRect;
    // flags
    private boolean inProgress;
    private boolean detonated = false;
    private boolean removed = false;


    /**
     * Instantiates a new Explosive.
     *
     * @param position the position to drop (render) explosive.
     */
    public Explosive(Point position) {
        this.position = position;
        this.inProgress = true;
        this.dropTime = System.currentTimeMillis();
        this.effectRect = new Rectangle(position.x-EFFECT_RADIUS, position.y-EFFECT_RADIUS, EFFECT_RADIUS, EFFECT_RADIUS);
    }

    // getter
    public int getDAMAGE() {
        return DAMAGE;
    }
    public static long getDetonationMs() {
        return DETONATION_MS;
    }
    public long getEntryTime() {
        return entryTime;
    }
    public boolean isDetonated() {
        return detonated;
    }
    public boolean isRemoved() {
        return removed;
    }


    // setter
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }


    /**
     * Returns a boolean value to indicate whether the given detonation time of explosive has passed
     *
     * @param detonationTime the given detonation time (affected by timescale)
     * @param dropTime       time when explosive is dropped
     * @param currentTime    the current time
     * @return the boolean
     */
    public boolean detonationTimePassed(long detonationTime, long dropTime, long currentTime) {
        return currentTime-dropTime >= (detonationTime*TO_MILLI);
    }


    /**
     * Update state of explosive.
     */
    public void update() {
        // drop the explosive
        if (inProgress) {
            IMAGE.draw(position.x, position.y);
        }
        // detonate the explosive
        if (inProgress && detonationTimePassed(DETONATION_TIME/ShadowDefend.getTimescale(),
                dropTime, System.currentTimeMillis())) {
            // mark flags
            detonated = true;
            inProgress = false;
            // add to hashmap of explosives in level
            entryTime = System.currentTimeMillis();
            Level.getExplosivesInUse().put(effectRect, this);
        }
    }
}
