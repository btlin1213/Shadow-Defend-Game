import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Window;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

public class BuyPanel {
    private final Font moneyFont;
    private final Font keybindFont;
    private final Font costFont;
    private final Image backgroundImage;
    private final Image tankImage;
    private final Image supertankImage;
    private final Image airplaneImage;
    private static Rectangle panelRect;
    private static final int MONEY_FONT_SIZE = 48;
    private static final int KEYBIND_FONT_SIZE = 14;
    private static final int COST_FONT_SIZE = 20;
    private static final int INITIAL_MONEY = 500;
    private static final int TOWER_LEFT_OFFSET = 64;
    private static final int TOWER_GAP = 120;
    private static final int TOWER_CENTRE_OFFSET = 10;
    private static final int COST_OFFSET = 10;
    private static final int KEYBIND_TOP_OFFSET = 20;
    private static final int KEYBIND_GAP = 15;
    private static final int MONEY_LEFT_OFFSET = 200;
    private static final int MONEY_DOWN_OFFSET = 65;
    private static final String TANK_COST = "$250";
    private static final String SUPER_TANK_COST = "$600";
    private static final String AIRPLANE_COST = "$500";
    private static final int TANK_COST_AMOUNT = 250;
    private static final int SUPER_TANK_COST_AMOUNT = 600;
    private static final int AIRPLANE_COST_AMOUNT = 500;
    private static final DrawOptions GREEN_SHADE = new DrawOptions().setBlendColour(Colour.GREEN);
    private static final DrawOptions RED_SHADE = new DrawOptions().setBlendColour(Colour.RED);
    private static final String KEYBINDS = "Key binds:";
    private static final String S_KEYBIND = "S - Start Wave";
    private static final String L_KEYBIND = "L - Increase Timescale";
    private static final String K_KEYBIND = "K - Decrease Timescale";
    private static Rectangle REGULAR_TANK_POSITION;
    private static Rectangle SUPER_TANK_POSITION;
    private static Rectangle AIRPLANE_POSITION;
    private static Point REGULAR_TANK_POINT;
    private static Point SUPER_TANK_POINT;
    private static Point AIRPLANE_POINT;
    private int money = INITIAL_MONEY;

    /**
     * Instantiates a new Buy panel.
     */
    public BuyPanel() {
        this.moneyFont = new Font("res/fonts/DejaVuSans-Bold.ttf", MONEY_FONT_SIZE);
        this.keybindFont = new Font("res/fonts/DejaVuSans-Bold.ttf", KEYBIND_FONT_SIZE);
        this.costFont = new Font("res/fonts/DejaVuSans-Bold.ttf", COST_FONT_SIZE);
        this.backgroundImage = new Image("res/images/buypanel.png");
        this.tankImage = new Image("res/images/tank.png");
        this.supertankImage = new Image("res/images/supertank.png");
        this.airplaneImage = new Image("res/images/airsupport.png");
        REGULAR_TANK_POSITION = tankImage.getBoundingBoxAt(new Point(TOWER_LEFT_OFFSET, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET));
        SUPER_TANK_POSITION = supertankImage.getBoundingBoxAt(new Point(TOWER_LEFT_OFFSET+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET));
        AIRPLANE_POSITION = airplaneImage.getBoundingBoxAt(new Point(TOWER_LEFT_OFFSET+TOWER_GAP+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET));
        REGULAR_TANK_POINT = new Point(TOWER_LEFT_OFFSET, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        SUPER_TANK_POINT = new Point(TOWER_LEFT_OFFSET+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        AIRPLANE_POINT = new Point(TOWER_LEFT_OFFSET+TOWER_GAP+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        panelRect = new Rectangle(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
    }

    // getters
    public static Rectangle getRegularTankPosition() {
        return REGULAR_TANK_POSITION;
    }
    public static Rectangle getSuperTankPosition() {
        return SUPER_TANK_POSITION;
    }
    public static Rectangle getAirplanePosition() {
        return AIRPLANE_POSITION;
    }
    public static Rectangle getPanelRect() {
        return panelRect;
    }
    public static Point getRegularTankPoint() {
        return REGULAR_TANK_POINT;
    }
    public static Point getSuperTankPoint() {
        return SUPER_TANK_POINT;
    }
    public static Point getAirplanePoint() {
        return AIRPLANE_POINT;
    }
    public static int getTankCost() {
        return TANK_COST_AMOUNT;
    }
    public static int getSuperTankCost() {
        return SUPER_TANK_COST_AMOUNT;
    }
    public static int getAirplaneCost() {
        return AIRPLANE_COST_AMOUNT;
    }


    /**
     * Render initial state of Buy Panel, including all elements that are to stay static throughout whole game.
     */
    public void renderInitial() {
        // draw background
        backgroundImage.draw((double) Window.getWidth()/2, backgroundImage.getHeight()/2);
        // draw tower images
        tankImage.draw(TOWER_LEFT_OFFSET, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        supertankImage.draw(TOWER_LEFT_OFFSET+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        airplaneImage.draw(TOWER_LEFT_OFFSET+TOWER_GAP+TOWER_GAP, backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET);
        // draw keybinds
        keybindFont.drawString(KEYBINDS, (double) Window.getWidth()/2-TOWER_CENTRE_OFFSET, KEYBIND_TOP_OFFSET);
        keybindFont.drawString(S_KEYBIND, (double) Window.getWidth()/2-TOWER_CENTRE_OFFSET,
                KEYBIND_TOP_OFFSET+2*KEYBIND_GAP);
        keybindFont.drawString(L_KEYBIND, (double) Window.getWidth()/2-TOWER_CENTRE_OFFSET,
                KEYBIND_TOP_OFFSET+3*KEYBIND_GAP);
        keybindFont.drawString(K_KEYBIND, (double) Window.getWidth()/2-TOWER_CENTRE_OFFSET,
                KEYBIND_TOP_OFFSET+4*KEYBIND_GAP);
    }


    /**
     * Helper method to render player's money.
     *
     * @param amount the numerical amount to render;
     */
    public void renderMoney(int amount) {
        // draw money
        String newMoneyString = "$" + String.valueOf(amount);
        moneyFont.drawString(newMoneyString, Window.getWidth()-MONEY_LEFT_OFFSET,
                MONEY_DOWN_OFFSET);
    }


    /**
     * Render updated state of dynamic elements,
     * such as shading of cost for each tower according to player's financial state.
     */
    public void renderUpdate() {
        money = ShadowDefend.getPlayer().getMoney();
        renderInitial();
        // cost of towers
        if (money >= getTankCost()) {
            costFont.drawString(TANK_COST, TOWER_LEFT_OFFSET-tankImage.getWidth()/3,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+tankImage.getHeight()/2+COST_OFFSET, GREEN_SHADE);
        }
        else {
            costFont.drawString(TANK_COST, TOWER_LEFT_OFFSET-tankImage.getWidth()/3,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+tankImage.getHeight()/2+COST_OFFSET, RED_SHADE);
        }
        if (money >= getSuperTankCost()) {
            costFont.drawString(SUPER_TANK_COST, TOWER_LEFT_OFFSET-supertankImage.getWidth()/3+TOWER_GAP,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+
                            supertankImage.getHeight()/2+COST_OFFSET+(double)COST_OFFSET/3, GREEN_SHADE);
        }
        else {
            costFont.drawString(SUPER_TANK_COST, TOWER_LEFT_OFFSET-supertankImage.getWidth()/3+TOWER_GAP,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+
                            supertankImage.getHeight()/2+COST_OFFSET+(double)COST_OFFSET/3, RED_SHADE);

        }
        if (money >= getAirplaneCost()) {
            costFont.drawString(AIRPLANE_COST, TOWER_LEFT_OFFSET-airplaneImage.getWidth()/3+TOWER_GAP+TOWER_GAP,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+airplaneImage.getHeight()/2+COST_OFFSET, GREEN_SHADE);
        }
         else {
            costFont.drawString(AIRPLANE_COST, TOWER_LEFT_OFFSET-airplaneImage.getWidth()/3+TOWER_GAP+TOWER_GAP,
                    backgroundImage.getHeight()/2-TOWER_CENTRE_OFFSET+airplaneImage.getHeight()/2+COST_OFFSET, RED_SHADE);
        }

        // money available
        renderMoney(money);
    }
}
