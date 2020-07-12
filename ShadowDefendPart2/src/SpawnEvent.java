import bagel.Input;
import bagel.Window;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

public class SpawnEvent extends WaveEvent {
    // event specs
    private List<Point> polylines;
    private Point polylineOrigin;
    private int numberToSpawn;
    private String enemyTypeString;
    private long spawnDelay;
    private long lastAddedTime;
    private int spawnedSlicers = 0;
    private ArrayList<Slicer> slicers = new ArrayList<Slicer>();
    // constants
    private static final String REGULAR_SLICER = "slicer";
    private static final String SUPER_SLICER = "superslicer";
    private static final String MEGA_SLICER = "megaslicer";
    private static int INITIAL_NEXT_POLYLINE_INDEX = 1;
    private final long INITIAL_SPAWN_DELAY;


    /**
     * Instantiates a new Spawn event.
     *
     * @param waveEventNumber the wave event number
     * @param numberToSpawn   the number of slicers to spawn
     * @param enemyTypeString the type of slicers to spawn
     * @param spawnDelay      the delay between each slicer
     * @param polylines       the polylines to traverse
     */
    public SpawnEvent(int waveEventNumber, int numberToSpawn, String enemyTypeString, long spawnDelay, List<Point> polylines) {
        super(waveEventNumber);
        this.numberToSpawn = numberToSpawn;
        this.enemyTypeString = enemyTypeString;
        this.INITIAL_SPAWN_DELAY = spawnDelay;
        this.spawnDelay = spawnDelay;
        this.polylines = polylines;
        this.polylineOrigin = polylines.get(0);
    }

    // getter
    public ArrayList<Slicer> getSlicers() {
        return slicers;
    }

    // setter
    public void setSpawnDelay(long spawnDelay) {
        this.spawnDelay = spawnDelay;
    }


    /**
     * Returns a boolean that indicates whether the required delay between each slicer spawn has passed.
     *
     * @param lastAddedTime time when last slicer was spawned
     * @param currentTime   the current time
     * @return the boolean mentioned above
     */
    public boolean delayPassed(long lastAddedTime, long currentTime) {
        if (timeDifference(lastAddedTime, currentTime) >= spawnDelay) {
            return true;
        }
        return false;
    }

    /**
     * Increases the count of spawned slicers.
     */
    public void increaseSpawnedSlicers() {
        spawnedSlicers++;
    }

    /**
     * Adds a newly spawned slicer to the list of slicers of this spawn event.
     */
    public void addToSlicerList() {
        // determine type of slicer and initialise it
        if (enemyTypeString.equals(REGULAR_SLICER)) {
            slicers.add(new RegularSlicer(polylines, polylineOrigin, INITIAL_NEXT_POLYLINE_INDEX));
        }
        else if (enemyTypeString.equals(SUPER_SLICER)) {
            slicers.add(new SuperSlicer(polylines, polylineOrigin, INITIAL_NEXT_POLYLINE_INDEX));
        }
        else if (enemyTypeString.equals(MEGA_SLICER)) {
            slicers.add(new MegaSlicer(polylines, polylineOrigin, INITIAL_NEXT_POLYLINE_INDEX));
        }
        else {
            slicers.add(new ApexSlicer(polylines, polylineOrigin, INITIAL_NEXT_POLYLINE_INDEX));
        }
        increaseSpawnedSlicers();
        // update last added time to now
        lastAddedTime = System.currentTimeMillis();
    }

    /**
     * Overrides the abstract method inherited from WaveEvent parent class.
     * Mark this wave event as started and in progress, add first slicer to spawn list.
     */
    @Override
    public void startWaveEvent() {
        // mark flags
        this.setWaveEventStarted(true);
        this.setWaveEventInProgress(true);
        // add first slicer to list
        addToSlicerList();
    }

    /**
     * Overrides the abstract method inherited from WaveEvent parent class.
     * Top-level overall update method for this spawn event. Updates the spawn event state until it finishes.
     *
     * @param input input from keyboard / mouse.
     */
    @Override
    public void updateWaveEvent(Input input) {
        // check if delay is updated
        setSpawnDelay(INITIAL_SPAWN_DELAY/ShadowDefend.getTimescale());
        // add appropriate type slicer to slicer arraylist if the delay has passed
        if (spawnedSlicers < numberToSpawn && delayPassed(lastAddedTime, System.currentTimeMillis())) {
            addToSlicerList();
        }
        // Update all slicers, and remove them if they've finished
        for (int i=slicers.size()-1; i >= 0; i--) {
            Slicer s = slicers.get(i);
            s.update(input);
            // slicer finished traversal without elimination, player lose life
            if (s.isFinished()) {
                ShadowDefend.getPlayer().setLives(ShadowDefend.getPlayer().getLives()-s.getPenalty());
                // player lost all lives, they lose
                if (ShadowDefend.getPlayer().getLives()<=0) {
                    Window.close();
                }
                // they can keep playing
                else {
                    slicers.remove(i);
                }
            }
            // slicer eliminated
            if (s.isEliminated()) {
                slicers.remove(s);
                // reward player
                ShadowDefend.getPlayer().setMoney(ShadowDefend.getPlayer().getMoney()+s.getReward());
                // if it's a not a regular slicer, add its child slicers to the slicers list
                if (!(s instanceof RegularSlicer)) {
                    slicers.addAll(0, s.spawnChildSlicers(input));
                }
            }
        }
        // if all slicers are finished, mark this wave event as finished
        if (slicers.size() == 0) {
            this.setWaveEventFinished(true);
            this.setWaveEventInProgress(false);
        }
    }
}