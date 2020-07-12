import bagel.util.Point;
import java.util.List;

public class Polyline {
    private List<Point> pointsList;
    private Point startingPoint;
    private Point endingPoint;
    private int length;

    // Constructor
    public Polyline(List<Point> allPoints) {
        pointsList = allPoints;
        startingPoint = allPoints.get(0);
        endingPoint = allPoints.get(allPoints.size()-1);
        length = allPoints.size();
    }

    // getter methods
    public Point getStartingPoint() {
        return this.startingPoint;
    }

    public int getLength() {
        return this.length;
    }

    public int getIndexOf(Point thisPoint) {
        return this.pointsList.indexOf(thisPoint);
    }

    public Point getPointAt(int index) { return this.pointsList.get(index); }
}
