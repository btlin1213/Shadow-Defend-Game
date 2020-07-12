import bagel.util.Point;

public class Wave {
    // wave specs
    private Polyline polylines;
    private boolean waveFinished;
    // speed specs
    private double waveSpeed;
    private double initialSpawnDelay;
    private double spawnDelay;
    // slicers array size specs
    private int waveSize;
    private Slicer[] slicerArray;
    // slicer array property specs
    private int slicersToFinish;


    // Constructor: Assigns all default values from ShadowDefend to attributes in this wave
    public Wave(Polyline polylines, double waveSpeed, double spawnDelay, int waveSize) {
        this.polylines = polylines;
        this.waveSpeed = waveSpeed;
        this.spawnDelay = spawnDelay;
        this.initialSpawnDelay = spawnDelay;
        this.waveSize = waveSize;
        this.slicersToFinish = waveSize;
    }

    // getter methods
    public boolean isAllFinished() { return waveFinished; }

    public double getInitialSpawnDelay() { return initialSpawnDelay; }


    // setter methods
    public void setWaveSpeed(double waveSpeed) {
        this.waveSpeed = waveSpeed;
        // speed will always equal to the timescale of whole game
        // spawn delay is always its initial value (5) divided by the timescale
        this.setSpawnDelay(this.getInitialSpawnDelay()/this.waveSpeed);
    }

    public void setSpawnDelay(double spawnDelay) {
        this.spawnDelay = spawnDelay;
    }

    // wave methods
    // Start a new wave by initialising an array of slicers of given wave size, and spawn the first slicer
    public void startWave() {
        slicerArray = new Slicer[waveSize];
        slicerArray[0] = spawnSlicer();
    }

    // Checking whether the slicer in this index of slicer array is ready to be spawned
    public boolean canBeSpawned(int i) {
        // Condition for spawning slicers indexed 1-4:previous slot has spawned and delay time has passed
        return slicerArray[i-1].isSpawned() && timeDifference(slicerArray[i-1].getSpawnTime(),
                System.currentTimeMillis()) >= spawnDelay;
    }

    // Facilitate for spawning slicers
    public Slicer spawnSlicer() {
        long spawnTime = System.currentTimeMillis();
        Slicer spawnedSlicer = new Slicer(polylines.getStartingPoint(),
                polylines.getIndexOf(polylines.getStartingPoint()), waveSpeed, spawnTime);
        spawnedSlicer.setSpawned(true);
        spawnedSlicer.render(spawnedSlicer.getSlicerLocation());
        return spawnedSlicer;
    }

    // Find time difference in SECONDS between 2 time stamps
    public long timeDifference(long earlierTimeMillis, long laterTimeMillis) {
        // Find difference between 2 time stamps in SECONDS
        return (laterTimeMillis - earlierTimeMillis) / 1000;
    }

    // update the slicer in each slot of the slicer array
    public void updateSlicerArrayStatus(Slicer currentSlicer) {
        // If the slicer in this slot of array is spawned and finished, do nothing

        // If the slicer in this slot has been spawned, and is traversing, but is not finished
        if (currentSlicer.isSpawned() && !currentSlicer.isFinished()) {
            // If the current position index is the last point in polyline, mark slicer as finished
            if (currentSlicer.getNextPositionIndex() == polylines.getLength()) {
                currentSlicer.setFinished(true);
                slicersToFinish -= 1;
            }
            // Otherwise, keep traversing
            else {
                Point currentPosition = currentSlicer.getSlicerLocation();
                Point nextPosition = polylines.getPointAt(currentSlicer.getNextPositionIndex());
                currentSlicer.setSlicerSpeed(waveSpeed);
                currentSlicer.setThreshold(waveSpeed);
                currentSlicer.update(currentPosition, nextPosition);
            }
        }
    }

    // update wave status
    public void update() {
        // There are 0 slicers left to finish traversal
        // Mark this wave as finished to stop calling update from ShadowDefend
        if (slicersToFinish == 0) {
            waveFinished = true;
        }

        else {
            // Iterate through each slicer in the slicer array
            for (int i=0; i<waveSize; i++) {

                // If this slicer is initialised, it is either 1. traversing, or, 2. finished.
                if (slicerArray[i] != null) {
                    // 1. Traversing, so update its traversal status.
                    if (!slicerArray[i].isFinished()) {
                        Slicer currentSlicer = slicerArray[i];
                        updateSlicerArrayStatus(currentSlicer);
                    }
                    // 2. Finished, do nothing and skip to next slicer array slot
                }

                // If this slicer has yet to be initialised (is null), it either 1. can be spawned or 2. not ready.
                else {
                    // 1. Can be spawned immediately, so spawn it.
                    // Only number 1-4 should be passed in here because index 0 is spawned at start of wave
                    if (canBeSpawned(i)) {
                        slicerArray[i] = spawnSlicer();
                    }
                    // 2. Must wait to be spawned, so break out of this for-loop as all slicers after it must wait too.
                    else {
                        break;
                    }
                }
            }
        }
    }
}
