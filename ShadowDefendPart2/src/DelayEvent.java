import bagel.Input;

public class DelayEvent extends WaveEvent {
    private int delayInMS;
    private long startTime;

    /**
     * Instantiates a new Delay event.
     *
     * @param waveNumber the wave number.
     * @param delayInMS  the delay in milliseconds.
     */
    public DelayEvent(int waveNumber, int delayInMS) {
        super(waveNumber);
        this.delayInMS = delayInMS;
    }

    /**
     * Start the delay event by recording the start of delay.
     * Mark event as started and in-progress.
     */
    @Override
    public void startWaveEvent() {
        // record the time of starting the delay
        startTime = System.currentTimeMillis();
        this.setWaveEventStarted(true);
        this.setWaveEventInProgress(true);
    }

    /**
     * Update delay event by checking current time. If required delay time with timescale influence has passed,
     * mark event as finished.
     *
     * @param input
     */
    @Override
    public void updateWaveEvent(Input input) {
        if (timeDifference(startTime, System.currentTimeMillis()) >= delayInMS) {
            // after enough time has passed, set this event as finished
            this.setWaveEventFinished(true);
        }
    }
}
