import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShadowDefend extends AbstractGame {
    // default and initial values
    int DEFAULT_WAVE_SIZE = 5;
    int DEFAULT_WAVE_SPEED = 1;
    int DEFAULT_SPAWN_DELAY = 5;
    // game attributes
    private final TiledMap map = new TiledMap("res/levels/1.tmx");
    private Polyline polylines = new Polyline(map.getAllPolylines().get(0));
    private Wave waveOne = new Wave(polylines, DEFAULT_WAVE_SPEED, DEFAULT_SPAWN_DELAY, DEFAULT_WAVE_SIZE);
    private int timescale = 1;
    private boolean waveInProgress = false;

    /**
     * Entry point for Bagel game
     *
     * Explore the capabilities of Bagel: https://people.eng.unimelb.edu.au/mcmurtrye/bagel-doc/
     */
    public static void main(String[] args) {
        // Create new instance of game and run it
        new ShadowDefend().run();
    }

    /**
     * Setup the game: Constructor
     */
    public ShadowDefend() {
        // fixing glitch
        new Image("res/images/slicer.png");
    }

    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {
        // draw the map
        map.draw(0, 0, 0, 0, Window.getWidth(), Window.getHeight());

        // TIMESCALE CHANGES (can be before wave starts)
        // if L is pressed, timescale + 1
        if (input.wasPressed(Keys.L)) {
            timescale += 1;
            waveOne.setWaveSpeed(timescale);
        }

        // if K is pressed, timescale - 1
        if (input.wasPressed(Keys.K) && timescale - 1 > 0) {
            timescale -= 1;
            waveOne.setWaveSpeed(timescale);
        }

        // START NEW WAVE, mark wave flag as true
        if (input.wasPressed(Keys.S) && !waveInProgress) {
            waveOne.startWave();
            waveInProgress = true;
        }

        // AMIDST WAVE, update wave until wave ends
        if (waveInProgress) {
            waveOne.update();
            if (waveOne.isAllFinished()) {
                // mark wave flag as false when wave ends
                waveInProgress = false;
            }
        }

        // WAVE FINISHED, close window
        if (!waveInProgress && waveOne.isAllFinished()) {
            Window.close();
        }
    }
}
