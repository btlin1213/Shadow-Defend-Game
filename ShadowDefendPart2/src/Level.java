import bagel.Input;
import bagel.Keys;
import bagel.MouseButtons;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Level {
    // level specs
    private TiledMap map;
    private List<Point> polylines;
    private final BuyPanel buyPanel = new BuyPanel();
    private final StatusPanel statusPanel = new StatusPanel();
    // constants
    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private static final int CONSTANT_REWARD = 150;
    private static final int WAVE_REWARD = 100;
    private final int END = 0;
    private final String H = "horizontal";
    private final String V = "vertical";
    // wave specs
    private final ArrayList<Wave> waves;
    private Wave currWaveInProgress;
    private int currWaveIndex = 0;
    private int wavesFinished = 0;
    private int wavesTotal;
    // tower specs
    private Tower selectedTower;
    private String direction;
    private ArrayList<Tower> towers = new ArrayList<Tower>();
    private static HashMap<Rectangle, Explosive> explosivesInUse = new HashMap<Rectangle, Explosive>();
    // flags
    private static boolean waveInProgress = false;
    private boolean levelInProgress = false;
    private boolean levelFinished = false;
    private static boolean mouseSelecting = false;


    /**
     * Instantiates a new Level.
     *
     * @param map       the map for this level.
     * @param polylines the polylines to traverse for this level.
     * @param waves     the waves of slicers.
     * @param direction the direction of first airplane.
     */
    public Level(TiledMap map, List<Point> polylines, ArrayList<Wave> waves, String direction) {
        this.map = map;
        this.polylines = polylines;
        this.waves = waves;
        this.wavesTotal = waves.size();
        this.direction = direction;
    }


    // getters
    public boolean isLevelInProgress() {
        return levelInProgress;
    }
    public boolean isLevelFinished() {
        return levelFinished;
    }
    public BuyPanel getBuyPanel() {
        return buyPanel;
    }
    public StatusPanel getStatusPanel() {
        return statusPanel;
    }
    public static HashMap<Rectangle, Explosive> getExplosivesInUse() {
        return explosivesInUse;
    }

    // setters
    public static void setWaveInProgress(boolean waveInProgress) {
        Level.waveInProgress = waveInProgress;
    }
    public static void setMouseSelecting(boolean mouseSelecting) {
        Level.mouseSelecting = mouseSelecting;
    }

    /**
     * Start level by resetting player's money and lives, and mark level in progress flag as true.
     */
    public void startLevel() {
        // reset Player stats
        ShadowDefend.getPlayer().setMoney(ShadowDefend.getPlayer().getSTART_MONEY());
        ShadowDefend.getPlayer().setLives(ShadowDefend.getPlayer().getSTART_LIVES());
        // mark flags
        levelInProgress = true;
    }

    /**
     * Render level map and panels.
     */
    public void renderLevel() {
        // draw the map
        map.draw(0, 0, 0, 0, WIDTH, HEIGHT);
    }

    /**
     * Increase the number of waves finished.
     */
    private void increaseWavesFinished() {
        this.wavesFinished += 1;
    }

    /**
     * Increase the index of current wave to process next wave.
     */
    private void increaseWaveIndex() {this.currWaveIndex++;}

    /**
     * Switch direction of next Airplane to place.
     *
     * @param prevDirection the previous Airplane direction.
     */
    public void switchDirection(String prevDirection) {
        if (prevDirection.equals(H)) {
            direction = V;
        }
        else if (prevDirection.equals(V)) {
            direction = H;
        }
    }


    /**
     * Update state of waves in this level.
     *
     * @param input The current mouse/keyboard state.
     */
    public void updateWave(Input input) {
        if (!waveInProgress) {
            // render status 4: awaiting start
            statusPanel.setStatus(StatusPanel.getStatusFour());
            // if S is pressed, find the next wave to start
            if (input.wasPressed(Keys.S)) {
                currWaveInProgress = waves.get(currWaveIndex);
                currWaveInProgress.startWave();
                statusPanel.setWaveNumber(currWaveInProgress.getWaveNumber());
                waveInProgress = true;
            }
        }
        if (currWaveInProgress != null && waveInProgress) {
            // update current wave
            currWaveInProgress.update(input);
            statusPanel.setStatus(StatusPanel.getStatusThree());
            statusPanel.renderUpdate();
            // if current wave finished
            if (currWaveInProgress.isWaveFinished()) {
                // mark flag
                waveInProgress = false;
                // reward player accordingly
                int totalReward = CONSTANT_REWARD + WAVE_REWARD * currWaveInProgress.getWaveNumber();
                Player.getPlayer().setMoney(Player.getPlayer().getMoney() + totalReward);
                // increase waves finished count
                increaseWavesFinished();
                // update index of wave in progress
                increaseWaveIndex();
            }
        }
    }

    /**
     * Update state of towers in this level.
     *
     * @param input The current mouse/keyboard state.
     */
    public void updateTowers(Input input) {
        Point mousePosition = input.getMousePosition();
        // update each tower in towers list
        if (towers.size() > 0) {
            for (int i=towers.size()-1; i>=0; i--) {
                Tower tower = towers.get(i);
                tower.update(input);
                // if tower = airplane, and all of its explosives are detonated, then remove it from game
                if (tower.getClass().equals(Airplane.class) && ((Airplane) tower).isAllDetonated()) {
                    towers.remove(tower);
                }
            }
        }

        // if no tower selected and player just left-clicked on a tower from BuyPanel then initialise it in towers list
        if (!mouseSelecting) {
            if (input.wasPressed(MouseButtons.LEFT)) {
                // initialise a new Regular Tank if clicked on and add to towers list
                if (BuyPanel.getRegularTankPosition().intersects(mousePosition) &&
                        ShadowDefend.getPlayer().getMoney() >= BuyPanel.getTankCost()) {
                    selectedTower = new RegularTank(BuyPanel.getRegularTankPoint(),
                            towers, polylines, map, new ArrayList<Projectile>());
                    ShadowDefend.getPlayer().setMoney(ShadowDefend.getPlayer().getMoney()- BuyPanel.getTankCost());

                }
                // initialise a new Super Tank if clicked on and add to towers list
                else if (BuyPanel.getSuperTankPosition().intersects(mousePosition) &&
                        ShadowDefend.getPlayer().getMoney() >= BuyPanel.getSuperTankCost()) {
                    selectedTower = new SuperTank(BuyPanel.getSuperTankPoint(),
                            towers, polylines, map, new ArrayList<Projectile>());
                    ShadowDefend.getPlayer().setMoney(ShadowDefend.getPlayer().getMoney()- BuyPanel.getSuperTankCost());
                }
                // initialise a new airplane if clicked on and add to towers list
                else if (BuyPanel.getAirplanePosition().intersects(mousePosition) &&
                        ShadowDefend.getPlayer().getMoney() >= BuyPanel.getAirplaneCost()) {
                    selectedTower = new Airplane(BuyPanel.getAirplanePoint(),
                            towers, polylines, map, direction);
                    ShadowDefend.getPlayer().setMoney(ShadowDefend.getPlayer().getMoney()-BuyPanel.getAirplaneCost());
                    switchDirection(direction);
                }

                // assign the selectedTower reference to the last added item
                if (selectedTower != null) {
                    selectedTower.setSelected(true);
                    statusPanel.setStatus(StatusPanel.getStatusTwo());
                    towers.add(selectedTower);
                    mouseSelecting = true;
                }
            }
        }

        // if the tower is placed, mark flags and update game status
        if (selectedTower != null && selectedTower.isPlaced()) {
            mouseSelecting = false;
            // reset status
            if (waveInProgress) {
                statusPanel.setStatus(StatusPanel.getStatusThree());
            }
            else {
                statusPanel.setStatus(StatusPanel.getStatusFour());
            }
        }

        // handle deselection
        if (mouseSelecting && input.wasPressed(MouseButtons.RIGHT)) {
            if (selectedTower != null) {
                // refund player money
                Player.getPlayer().setMoney(Player.getPlayer().getMoney()+selectedTower.getCost());
                // remove tower from list
                towers.remove(selectedTower);
                // mark flag
                mouseSelecting = false;
            }
            // update status
            if (waveInProgress) {
                statusPanel.setStatus(StatusPanel.getStatusThree());
            }
            else {
                statusPanel.setStatus(StatusPanel.getStatusFour());
            }
        }
    }

    /**
     * Top-level overall update method for this level,
     * calls updateWave and updateTowers.
     *
     * @param input The current mouse/keyboard state.
     */
    public void update(Input input) {
        // check if there are still lives
        if (ShadowDefend.getPlayer().getLives()==0) {
            statusPanel.renderLives(END);
            Window.close();
        }

        // draw the map and panels
        renderLevel();
        // update panel stats
        statusPanel.setTimescale(ShadowDefend.getTimescale());
        statusPanel.setLives(ShadowDefend.getPlayer().getLives());
        // render panels
        buyPanel.renderUpdate();
        statusPanel.renderUpdate();
        // TOWER UPDATE
        updateTowers(input);
        // WAVE UPDATE
        updateWave(input);

        // if all waves are finished, ShadowDefend should level up
        if (this.wavesFinished == this.wavesTotal) {
            // update status
            statusPanel.setStatus(StatusPanel.getStatusOne());
            statusPanel.renderUpdate();
            // mark flags
            Level.setWaveInProgress(false);
            levelInProgress = false;
            levelFinished = true;
        }
    }
}
