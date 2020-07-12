import bagel.Input;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;


public class Wave {
    // wave specs
    private List<Point> polylines;
    private int waveNumber;
    private int eventsTotal;
    private int eventsFinished = 0;
    private int currEventIndex = 0;
    private WaveEvent currEventInProgress;
    private ArrayList<WaveEvent> waveEvents = new ArrayList<WaveEvent>();
    // flags
    private boolean waveFinished = false;
    private boolean eventInProgress = false;


    /**
     * Instantiates a new Wave.
     *
     * @param waveNumber the wave number
     * @param polylines  the polylines
     */
    public Wave(int waveNumber, List<Point> polylines) {
        this.waveNumber = waveNumber;
        this.polylines = polylines;
    }

    // getter
    public ArrayList<WaveEvent> getWaveEvents() {
        return waveEvents;
    }
    public List<Point> getPolylines() {
        return polylines;
    }
    public int getWaveNumber() {
        return waveNumber;
    }
    public boolean isWaveFinished() {
        return waveFinished;
    }


    /**
     * Start wave by equating total count of to-process events to the size of WaveEvents arraylist.
     */
    public void startWave() {
        this.eventsTotal = waveEvents.size();
    }

    /**
     * Increase the count of wave events finished by one.
     */
    private void increaseEventFinished() {
        this.eventsFinished++;
    }

    /**
     * Increase the index of current event by one.
     */
    private void increaseEventIndex() {
        this.currEventIndex++;
    }


    /**
     * Top-level overall update method for the wave class.
     * Responsible for updating the wave until it finishes.
     *
     * @param input the input from keyboard / mouse.
     */
    public void update(Input input) {
        // if no event in progress, start next event
        if (!eventInProgress) {
            // start next wave event
            currEventInProgress = waveEvents.get(currEventIndex);
            currEventInProgress.startWaveEvent();
            ShadowDefend.setCurrentWaveEvent(currEventInProgress);
            eventInProgress = true;
        }
        // if event in progress, update current event
        if (currEventInProgress != null && eventInProgress) {
            currEventInProgress.updateWaveEvent(input);
            // if current event finished
            if (currEventInProgress.isWaveEventFinished()) {
                // mark flag
                eventInProgress = false;
                // increase events finished count
                increaseEventFinished();
                // update index of event in progress
                increaseEventIndex();
            }
        }

        if (this.eventsFinished == this.eventsTotal) {
            waveFinished = true;
        }
    }
}
