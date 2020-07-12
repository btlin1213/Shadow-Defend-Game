import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Colour;
import bagel.util.Rectangle;


public class StatusPanel {
    private final Image backgroundImage;
    private final Font font;
    private static final int FONT_SIZE = 20;
    private static final int INITIAL_WAVE = 1;
    private static final int INITIAL_TIMESCALE = 1;
    private static final int INITIAL_LIVES = 25;
    private static final int WAVE_LEFT_OFFSET = 30;
    private static final int TIME_SCALE_OFFSET = 180;
    private static final int LIVES_RIGHT_FACTOR = 5;
    private static final int LIVES_RIGHT_OFFSET = 100;
    private static final int HEIGHT_OFFSET = Window.getHeight()-5;
    private static final DrawOptions GREEN_SHADE = new DrawOptions().setBlendColour(Colour.GREEN);
    private static Rectangle panelRect;
    // statusOne > statusFour in priority
    private static final String STATUS_ONE = "Status: Winner!";
    private static final String STATUS_TWO = "Status: Placing";
    private static final String STATUS_THREE = "Status: Wave In Progress";
    private static final String STATUS_FOUR = "Status: Awaiting Start";
    private static final String WAVE = "Wave: ";
    private static final String TIMESCALE = "Time Scale: ";
    private static final String LIVES = "Lives: ";
    // editable specs
    private int waveNumber = INITIAL_WAVE;
    private int timescale = INITIAL_TIMESCALE;
    private String status = STATUS_FOUR;
    private int lives = INITIAL_LIVES;

    /**
     * Instantiates a new Status panel.
     */
    public StatusPanel() {
        this.backgroundImage = new Image("res/images/statuspanel.png");
        this.font = new Font("res/fonts/DejaVuSans-Bold.ttf", FONT_SIZE);
        panelRect = new Rectangle(0, Window.getHeight()-backgroundImage.getHeight(), backgroundImage.getWidth(), backgroundImage.getHeight());
    }

    // getters
    public int getTimescale() {
        return timescale;
    }
    public static String getStatusOne() {
        return STATUS_ONE;
    }
    public static String getStatusTwo() {
        return STATUS_TWO;
    }
    public static String getStatusThree() {
        return STATUS_THREE;
    }
    public static String getStatusFour() {
        return STATUS_FOUR;
    }
    public static Rectangle getPanelRect() {
        return panelRect;
    }

    // setters
    public void setTimescale(int timescale) {
        this.timescale = timescale;
    }
    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }


    /**
     * Render wave number (helper method).
     *
     * @param waveNumber the wave number to be rendered.
     */
    public void renderWave(int waveNumber) {
        String initialWaveString = WAVE + String.valueOf(waveNumber);
        font.drawString(initialWaveString, WAVE_LEFT_OFFSET, HEIGHT_OFFSET);
    }

    /**
     * Render timescale (helper method).
     *
     * @param timescale the timescale to be rendered.
     */
    public void renderTimescale(double timescale) {
        String initialTimescaleString = TIMESCALE + String.valueOf(timescale);
        if (timescale == INITIAL_TIMESCALE) {
            font.drawString(initialTimescaleString, WAVE_LEFT_OFFSET+TIME_SCALE_OFFSET, HEIGHT_OFFSET);
        }
        else {
            font.drawString(initialTimescaleString, WAVE_LEFT_OFFSET+TIME_SCALE_OFFSET, HEIGHT_OFFSET, GREEN_SHADE);
        }
    }

    /**
     * Render status (helper method).
     *
     * @param status the status to be rendered.
     */
    public void renderStatus(String status) {
        font.drawString(status, Window.getWidth()-LIVES_RIGHT_FACTOR*LIVES_RIGHT_OFFSET, HEIGHT_OFFSET);
    }

    /**
     * Render number of lives left (helper method).
     *
     * @param lives the number of lives left to be rendered.
     */
    public void renderLives(int lives) {
        String initialLivesString = LIVES + String.valueOf(lives);
        font.drawString(initialLivesString, Window.getWidth()-LIVES_RIGHT_OFFSET, HEIGHT_OFFSET);
    }

    /**
     * Render background of status panel (helper method).
     */
    public void renderBackground() {
        backgroundImage.draw((double) Window.getWidth()/2, Window.getHeight() - backgroundImage.getHeight()/2);
    }

    /**
     * Render initial state of status panel.
     */
    public void renderInitial() {
        // draw background
        renderBackground();
        // draw wave string
        renderWave(INITIAL_WAVE);
        // draw time scale string
        renderTimescale(INITIAL_TIMESCALE);
        // draw lives string
        renderLives(INITIAL_LIVES);
    }

    /**
     * Render updated state of status panel by calling previous helper methods.
     */
    public void renderUpdate() {
        renderBackground();
        renderWave(waveNumber);
        renderTimescale(timescale);
        renderStatus(status);
        renderLives(lives);
    }
}

