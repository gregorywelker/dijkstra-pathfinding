import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static int p, n, e; // p - number of point pairs to find routes between, n - number of crossings, e
                        // - number of connections

    static List<List<Integer>> search_pairs = new ArrayList<List<Integer>>();
    static List<List<Integer>> position_pairs = new ArrayList<List<Integer>>();
    static List<List<Integer>> crossing_connections = new ArrayList<List<Integer>>();

    static List<Crossing> crossings = new ArrayList<Crossing>();

    public static void main(String[] args) throws Exception {

        Scanner s = new Scanner(System.in);

        p = Integer.parseInt(s.next());
        n = Integer.parseInt(s.next());
        e = Integer.parseInt(s.next());

        for (int i = 0; i < p; i++) {
            List<Integer> pair = new ArrayList<Integer>();
            pair.add(Integer.parseInt(s.next()));
            pair.add(Integer.parseInt(s.next()));
            search_pairs.add(pair);
        }

        for (int i = 0; i < n; i++) {
            List<Integer> pair = new ArrayList<Integer>();
            pair.add(Integer.parseInt(s.next()));
            pair.add(Integer.parseInt(s.next()));
            position_pairs.add(pair);
        }

        for (int i = 0; i < e; i++) {
            List<Integer> pair = new ArrayList<Integer>();
            pair.add(Integer.parseInt(s.next()));
            pair.add(Integer.parseInt(s.next()));
            crossing_connections.add(pair);
        }

        for (int i = 0; i < n; i++) {
            crossings.add(new Crossing(position_pairs.get(i).get(0), position_pairs.get(i).get(1)));
        }

        for (int i = 0; i < e; i++) {
            crossings.get(crossing_connections.get(i).get(0)).connections
                    .add(crossings.get(crossing_connections.get(i).get(1)));
            crossings.get(crossing_connections.get(i).get(1)).connections
                    .add(crossings.get(crossing_connections.get(i).get(0)));
        }

        for (int i = 0; i < p; i++) {
            System.out
                    .print(String.format("%.2f",
                            Math.round(GetShortestPathDistance(crossings.get(search_pairs.get(i).get(0)),
                                    crossings.get(search_pairs.get(i).get(1))) * 100.0) / 100.0)
                            + (i < p - 1 ? "\t" : ""));
        }

        s.close();
    }

    public static double DistanceCalc(List<Crossing> path) {
        // Calculate distance through the path between each node
        double distance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            distance += path.get(i).Distance(path.get(i + 1));
        }
        return distance;
    }

    // Pathfinding based on Dijkstra`s algorithm - works like charm, if it is a good
    // base for the OSPF protocol, it`s gonna be good for me too. I have tried DFS,
    // Iterative Deepening DFS and A* but with the first two I ran out of time, and
    // with A* my heuristics was a bit off some of the time
    public static double GetShortestPathDistance(Crossing start, Crossing target) {

        // Empty unvisited list
        List<Crossing> unvisited = new ArrayList<Crossing>();

        // Copy the nodes into the unvisited list
        for (int i = 0; i < crossings.size(); i++) {
            unvisited.add(crossings.get(i));
        }

        // Set all node`s distance to infinity and their previous node to null
        for (int i = 0; i < unvisited.size(); i++) {
            unvisited.get(i).distance = Double.POSITIVE_INFINITY;
            unvisited.get(i).prev = null;
        }
        // Set the start node to distance 0, as distance from start to start is 0
        unvisited.get(unvisited.indexOf(start)).distance = 0;

        // We go till there are unvisited nodes, thecnically no, we go till we reach the
        // target node
        while (!unvisited.isEmpty()) {
            // Finding the node with the smallest distance as our next node
            Crossing closest = unvisited.get(0);
            for (int i = 0; i < unvisited.size(); i++) {
                if (unvisited.get(i).distance < closest.distance) {
                    closest = unvisited.get(i);
                }
            }
            // Remove the found node from the unvisited list
            unvisited.remove(closest);

            // If the found node is the target then we are good to go, we found the sortest
            // path
            if (closest.equals(target)) {
                // List for storing nodes
                List<Crossing> path = new ArrayList<Crossing>();
                path.add(closest);
                // Propagating back to the start node using the prev nodes
                while (closest.prev != null) {
                    path.add(closest.prev);
                    closest = closest.prev;
                }
                // Returning the found distance
                return DistanceCalc(path);
            }
            // If the node was not the target node then process its connections that are
            // still in the unprocessed list
            for (int i = 0; i < closest.connections.size(); i++) {
                if (unvisited.contains(closest.connections.get(i))) {
                    // The new distance is based on the distance so far and the Euclidean distance
                    // between the two connected nodes
                    double newdist = closest.distance + closest.Distance(closest.connections.get(i));
                    // If the new distance is smaller than the saved distance then update it
                    if (newdist < closest.connections.get(i).distance) {
                        closest.connections.get(i).distance = newdist;
                        closest.connections.get(i).prev = closest;
                    }
                }
            }
        }
        // Return zero if no target node was reached, with the assumption that there
        // exists a path between any start-target pairs the algorithm should not reach
        // this point
        return 0.0;
    }

}
