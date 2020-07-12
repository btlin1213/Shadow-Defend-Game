import bagel.Input;

public abstract class WaveEvent {
    private int waveEventNumber;
    private boolean waveEventStarted = false;
    private boolean waveEventInProgress = false;
    private boolean waveEventFinished = false;

    /**
     * Instantiates a new Wave event.
     *
     * @param waveEventNumber the wave event number.
     */
    public WaveEvent(int waveEventNumber) {
        this.waveEventNumber = waveEventNumber;
    }


    /**
     * Start wave event.
     * Abstract as spawn event adds its first slicer to the spawned list, whereas delay event just starts a timer.
     */
    public abstract void startWaveEvent();

    /**
     * Abstract method to update wave event, to be implemented in detail in subclasses (SpawnEvent and DelayEvent).
     * Abstract because a spawn event requires different updates (spawning slicers etc.)
     * whereas delay event just waits for time to pass.
     *
     * @param input input from keyboard / mouse.
     */
    public abstract void updateWaveEvent(Input input);

    // getter
    public boolean isWaveEventFinished() {
        return waveEventFinished;
    }

    // setter
    public void setWaveEventFinished(boolean waveEventFinished) {
        this.waveEventFinished = waveEventFinished;
    }
    public void setWaveEventStarted(boolean waveEventStarted) {
        this.waveEventStarted = waveEventStarted;
    }
    public void setWaveEventInProgress(boolean waveEventInProgress) {
        this.waveEventInProgress = waveEventInProgress;
    }

    /**
     * Find time difference in milliseconds between two given time stamps
     *
     * @param earlierTimeMillis the earlier timestamp in milliseconds
     * @param laterTimeMillis   the later timestamp in milliseconds
     * @return the difference in milliseconds
     */
    public long timeDifference(long earlierTimeMillis, long laterTimeMillis) {
        return (laterTimeMillis - earlierTimeMillis);
    }
}
