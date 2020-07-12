// some snippets are adapted from Project 1 Solution (ArrayList version)
import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ShadowDefend, a tower defense game for SWEN20003 Sem 1 2020
 * some code snippets are adapted from Rohyl's Project 1 Solution (ArrayList version)
 *
 * @author Dian Lin
 */
public class ShadowDefend extends AbstractGame {
    // game specs
    private static Player player = Player.getPlayer();
    private ArrayList<Level> levels = new ArrayList<Level>();
    private final String WAVE_FILE_NAME = "res/levels/waves.txt";
    private static final int INITIAL_TIMESCALE = 1;
    private static int timescale = INITIAL_TIMESCALE;
    private Level currentLevel;
    private int currentLevelIndex = 0;
    private int finishedLevels = 0;
    private int totalLevels;
    private static WaveEvent currentWaveEvent;
    // other constants
    private final int NUMBER_INDEX = 0;
    private final int TYPE_INDEX = 1;
    private final int SPAWN_INDEX = 2;
    private final int ENEMY_TYPE_INDEX = 3;
    private final int SPAWN_DELAY_INDEX = 4;
    private final int DELAY_TIME_INDEX = 2;
    private final String SPAWN = "spawn";
    private final String H = "horizontal";
    private final String V = "vertical";
    // level specs
    private final TiledMap LEVEL_1_MAP = new TiledMap("res/levels/1.tmx");
    private final TiledMap LEVEL_2_MAP = new TiledMap("res/levels/2.tmx");
    private final List<Point> LEVEL_1_POLYLINE = LEVEL_1_MAP.getAllPolylines().get(0);
    private final List<Point> LEVEL_2_POLYLINE = LEVEL_2_MAP.getAllPolylines().get(0);
    private final ArrayList<Wave> levelOneWaves = ParseWave(WAVE_FILE_NAME, LEVEL_1_POLYLINE);
    private final ArrayList<Wave> levelTwoWaves = ParseWave(WAVE_FILE_NAME, LEVEL_2_POLYLINE);
    private final Level levelOne = new Level(LEVEL_1_MAP, LEVEL_1_POLYLINE, levelOneWaves, H);
    private final Level levelTwo = new Level(LEVEL_2_MAP, LEVEL_2_POLYLINE, levelTwoWaves, H);




    /**
     * Instantiates a new Shadow Defend game.
     *
     * @throws IOException the io exception.
     */
    public ShadowDefend() throws IOException {
        // fixing glitch
        new Image("res/images/slicer.png");
        // adding two given levels to arraylist of levels
        levels.add(levelOne);
        levels.add(levelTwo);
        // set total number of levels to conquer as size of levels list
        this.totalLevels = levels.size();
    }

    /**
     * Entry-point for the game
     *
     * @param args Optional command-line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Create new instance of game and run it
        new ShadowDefend().run();

    }


    // getter methods
    public static int getTimescale() {
        return timescale;
    }
    public static WaveEvent getCurrentWaveEvent() {
        return currentWaveEvent;
    }
    public static Player getPlayer() {
        return player;
    }


    // setter methods
    public static void setCurrentWaveEvent(WaveEvent currentWaveEvent) {
        ShadowDefend.currentWaveEvent = currentWaveEvent;
    }


    /**
     * Increases the timescale
     */
    private void increaseTimescale() {
        timescale++;
    }


    /**
     * Decreases the timescale but doesn't go below the base timescale
     */
    private void decreaseTimescale() {
        if (timescale > INITIAL_TIMESCALE) {
            timescale--;
        }
    }

    /**
     * Increase the index of level to process
     */
    private void increaseLevelIndex() {
        currentLevelIndex++;
    }

    /**
     * Increase the count of finished levels
     */
    private void increaseFinishedLevels() {
        finishedLevels++;
    }

    /**
     * Parse wave array list and return an arraylist of wave objects.
     *
     * @param fileName  the file name ("waves.txt").
     * @param polylines the polylines provided by map for each level .
     * @return the array list of wave objects.
     * @throws IOException the io exception.
     */
    public ArrayList<Wave> ParseWave(String fileName, List<Point> polylines) throws IOException {
        ArrayList<Wave> waves = new ArrayList<Wave>();
        BufferedReader csvReader = new BufferedReader(new FileReader(fileName));
        int currWaveNumber = 0;
        for (String row = csvReader.readLine(); row != null; row = csvReader.readLine()) {
            String[] waveEventData = row.split(",");
            int waveEventNumber = Integer.parseInt(waveEventData[NUMBER_INDEX]);
            // if up to a new wave, make a new wave and add to wave list
            if (waveEventNumber > currWaveNumber) {
                currWaveNumber = waveEventNumber;
                waves.add(new Wave(currWaveNumber, polylines));
            }
            // add all wave events to their wave's event list
            if (waveEventData[TYPE_INDEX].equals(SPAWN)) {
                int numberToSpawn = Integer.parseInt(waveEventData[SPAWN_INDEX]);
                String enemyType = waveEventData[ENEMY_TYPE_INDEX];
                long spawnDelay = Long.parseLong(waveEventData[SPAWN_DELAY_INDEX]);
                // initialise new spawn event and add to wave event list
                waves.get(waveEventNumber-1).getWaveEvents().add(new SpawnEvent(waveEventNumber,
                        numberToSpawn, enemyType, spawnDelay, polylines));
            }
            else {
                int delayInMS = Integer.parseInt(waveEventData[DELAY_TIME_INDEX]);
                // initialise new delay event
                waves.get(waveEventNumber-1).getWaveEvents().add(new DelayEvent(waveEventNumber, delayInMS));
            }
        }
        return waves;
    }


    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     *
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {
        // keyboard inputs (acceleration / deceleration)
        if (input.wasPressed(Keys.L)) {
            increaseTimescale(); // passed down to level already
        }
        if (input.wasPressed(Keys.K)) {
            decreaseTimescale(); // passed down to level already
        }


        // if last level, end game
        if (totalLevels == finishedLevels) {
            currentLevel.renderLevel();
            currentLevel.getStatusPanel().setStatus(StatusPanel.getStatusOne());
            currentLevel.getBuyPanel().renderUpdate();
            currentLevel.getStatusPanel().renderUpdate();
        }
        // if not last level, keep playing
        else {
            // retrieve current level
            currentLevel = levels.get(currentLevelIndex);
            // if not started, then start it
            if (!currentLevel.isLevelInProgress() && !currentLevel.isLevelFinished()) {
                currentLevel.startLevel();
            }
            // if started and not finished, then update it
            if (currentLevel.isLevelInProgress()) {
                currentLevel.update(input);
            }
            // if finished and is not last level, then move to next level
            if (currentLevel.isLevelFinished()) {
                increaseLevelIndex();
                increaseFinishedLevels();
            }
        }
    }
}



