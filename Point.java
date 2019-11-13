import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

// Class setup to hold useful information about point objects
class Point {
    public Point(double x, double y, String type, Point connectsTo, Circle node, Line line) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.connectsTo = connectsTo;
        this.node = node;
        this.line = line;
    }

    public double x;
    public double y;
    public String type;
    public Point connectsTo;
    public Circle node;
    public Line line;

}
