import java.util.ArrayList;
import java.util.List;

public class Crossing {

    // Location of the crossing
    public int x, y;
    // Connections of the crossing
    public List<Crossing> connections = new ArrayList<Crossing>();
    // Distance from the start node
    public double distance;
    // Previous node
    public Crossing prev;

    public Crossing(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Euclidean distance
    public double Distance(Crossing other) {
        int x = this.x - other.x, y = this.y - other.y;
        return Math.sqrt(x * x + y * y);
    }
}
