public class Player {
    private final int START_LIVES = 25;
    private final int START_MONEY = 500;
    private int lives = START_LIVES;
    private int money = START_MONEY;
    private static Player instance = null;

    /**
     * Returns a Player instance. Can only be used within Player class.
     */
    private Player() {
    }

    /**
     * Returns the Player instance created within Player class.
     * Only access point to Player instance for other classes. (Singleton Pattern)
     *
     * @return THE player instance.
     */
    public static Player getPlayer() {
        if (instance == null) {
            instance = new Player();

        }
        return instance;
    }

    // getter
    public int getLives() {
        return lives;
    }
    public int getMoney() {
        return money;
    }
    public int getSTART_LIVES() {
        return START_LIVES;
    }
    public int getSTART_MONEY() {
        return START_MONEY;
    }

    // setter
    public void setLives(int lives) {
        this.lives = lives;
    }
    public void setMoney(int money) {
        this.money = money;
    }

}
