import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class Tower {
    // tower specs
    private Image towerImage;
    private Rectangle towerRect;
    private double angle;
    private Point position;
    private ArrayList<Tower> towers;
    private List<Point> polylines;
    private TiledMap map;
    private static final String BLOCKED_PROPERTY = "blocked";
    private int cost;
    // flags
    private boolean selected = false;
    private boolean placed = false;

    /**
     * Constructor for a tower (Abstract).
     *
     * @param position   the starting position of tower.
     * @param towerImage the tower image.
     * @param towers     the towers available in this level of the game right now.
     * @param polylines  the polylines of this level.
     * @param map        the map of this level.
     */
    public Tower(Point position, Image towerImage, ArrayList<Tower> towers, List<Point> polylines, TiledMap map, int cost) {
        this.towerImage = towerImage;
        this.towerRect = towerImage.getBoundingBoxAt(position);
        this.angle = 0;
        this.position = position;
        this.towers = towers;
        this.polylines = polylines;
        this.map = map;
        this.cost = cost;
    }

    /**
     * Gets center of bounding box of this tower.
     *
     * @return the center point.
     */
    public Point getCenter() {
        return getTowerRect().centre();
    }


    // getter
    public double getAngle() {
        return angle;
    }
    public Point getPosition() {
        return position;
    }
    public boolean isSelected() {
        return selected;
    }
    public boolean isPlaced() {
        return placed;
    }
    public Rectangle getTowerRect() {
        return towerRect;
    }
    public Image getTowerImage() {
        return towerImage;
    }
    public int getCost() {return cost;}

    // setter
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void setPosition(Point position) {
        this.position = position;
        this.towerRect = towerImage.getBoundingBoxAt(position);
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
    public void setTowerRect(Rectangle towerRect) {
        this.towerRect = towerRect;
    }


    /**
     * Update a tower when it is placed,
     * abstract due to difference between active and passive towers.
     *
     * @param input the input from mouse / keyboard.
     */
    public abstract void updatePlaced(Input input);

    /**
     * Top-level overall update method to be implemented in detail in subclasses,
     * abstract due to difference between active and passive towers.
     *
     * @param input the input from mouse / keyboard.
     */
    public abstract void update(Input input);


    /**
     * Returns a boolean to indicate whether a tower can be placed on this point.
     * Adapted from Week 5 Workshop Solution.
     *
     * @param point     the point of mouse location right now.
     * @param rectangle the rectangle of buy or status panel.
     * @param towers    the towers in this level right now.
     * @param polylines the polylines of this level.
     * @param map       the map of this level.
     * @return the boolean value mentioned above.
     */
    public boolean validPlacingPoint(Point point, Rectangle rectangle, ArrayList<Tower> towers,
                                     List<Point> polylines, TiledMap map) {
        // checking bound
        boolean invalidX = point.x < 0 || point.x > Window.getWidth();
        boolean invalidY = point.y < 0 || point.y > Window.getHeight();
        boolean outOfBounds = invalidX || invalidY;
        if (outOfBounds) {
            return false;
        }
        // checking if tower image rectangle intersect with panel
        boolean intersectPanel = BuyPanel.getPanelRect().intersects(rectangle) || StatusPanel.getPanelRect().intersects(rectangle);
        if (intersectPanel) {
            return false;
        }
        // checking if intersect with another tower (that is not current one)
        for (Tower tower : towers) {
            Rectangle towerRect = tower.getTowerRect();
            if (rectangle.intersects(towerRect)) {
                return false;
            }
        }
        // checking if intersect with polyline
        for (Point polylinePoint : polylines) {
            if (rectangle.intersects(polylinePoint)) {
                return false;
            }
        }
        // checking if tile is blocked
        return !map.getPropertyBoolean((int) point.x, (int) point.y, BLOCKED_PROPERTY, false);
    }


    /**
     * Update tower when it is selected but not placed yet.
     *
     * @param input input from keyboard/mouse.
     */
    public void updateSelection(Input input) {
        Point mousePosition = input.getMousePosition();

        if (validPlacingPoint(mousePosition, towerImage.getBoundingBoxAt(mousePosition), towers, polylines, map)) {
            // render the selected tower wherever mouse goes as long as it's a valid point
            towerImage.draw(mousePosition.x, mousePosition.y);
        }
        // if left-clicked on valid spot while a tower is selected, place the tower there
        if (input.wasPressed(MouseButtons.LEFT) && validPlacingPoint(mousePosition, towerImage.getBoundingBoxAt(mousePosition),
                towers, polylines, map)) {
            setPlaced(true);
            setPosition(mousePosition);
            setTowerRect(towerImage.getBoundingBoxAt(position));
            Level.setMouseSelecting(false);
        }
    }
}
