import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/*
 * name: Daniel Gil
 * purpose: to create a program that will produce the best path for a weighted directed graph.
 * The graph will be given in a .mtx file and will contain % as comments.
 * after that it will have integers mapping to another integer and then a weight.
 * I read this and store it in as a DGraph object.
 * and the main class will use different algorithms depending on 
 * the traverse chosen in args parameters and give the path that the traverse finds.
 */
public class Main {

    public static void main(String[] args) {

        // create scanner file
        Scanner scan = null;
        try {
            scan = new Scanner(new File(args[0]));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // store mtx in DGraph object
        DGraph graph = mtxToGraph(scan);

        // algorithm chosen
        if (args[1].equals("HEURISTIC")) {
            System.out.println(heuristic(graph).toString(graph));
        }
        else if (args[1].equals("BACKTRACK")) {
            System.out.println(backtrack(graph).toString(graph));
        } else if (args[1].equals("MINE")) {
            System.out.println(mine(graph).toString(graph));
        } else if (args[1].equals("TIME")) {
            timeTraversals(graph);
        }
        else {
            System.out.println("Invalid input");
            return;
        }

    }

    /*
     * function that will call the other time functions
     */
    public static void timeTraversals(DGraph graph) {
        heuristicTime(graph);
        backtrackTime(graph);
        mineTime(graph);
    }

    /*
     * prints out the amount of time it takes for my traversal
     * to work through in ms
     */
    private static void mineTime(DGraph graph) {
        // backtrack time
        long startTime = System.nanoTime();
        Trip trip = mine(graph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("mine: cost = " + trip.tripCost(graph) + ", "
                + duration + " milliseconds");
    }

    /*
     * prints out the amount of time it takes for backtrack traversal
     * to work through in ms
     */
    private static void backtrackTime(DGraph graph) {
        // backtrack time
        long startTime = System.nanoTime();
        Trip trip = backtrack(graph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("backtrack: cost = " + trip.tripCost(graph) + ", "
                + duration + " milliseconds");
    }

    /*
     * prints out the amount of time it takes for heuristic traversal
     * to work through in ms
     */
    private static void heuristicTime(DGraph graph) {
        // heuristic time
        long startTime = System.nanoTime();
        Trip trip = heuristic(graph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("heuristic: cost = " + trip.tripCost(graph) + ", "
                + duration + " milliseconds");
    }

    /*
     * finds the shortest path using a backtracking traversal
     * with pruning.
     * returns mintrip which is the smallest path in mtx
     */
    public static Trip mine(DGraph graph) {
        // create trip
        Trip trip = new Trip(graph.getNumNodes());
        int currentCity = 1;
        trip.chooseNextCity(currentCity);

        Trip minTrip = new Trip(graph.getNumNodes());
        mineHelper(graph, trip, minTrip);

        return minTrip;
    }

    /*
     * wrapper function for mine traversal.
     * traverses the DGraph object finding the smallest path possible
     * and prunes out whatever path is greater than the cur mintrip
     */
    private static void mineHelper(DGraph graph, Trip trip, Trip minTrip) {
        if (trip.citiesLeft().size() == 0) {
            if (trip.tripCost(graph) < minTrip.tripCost(graph)) {
                minTrip.copyOtherIntoSelf(trip);
            }
            return;
        }
        if (trip.tripCost(graph) < minTrip.tripCost(graph)) {
            for (int city : trip.citiesLeft()) {
                // choose
                trip.chooseNextCity(city);

                // do not recurse if it is a bad path
                if (trip.tripCost(graph) > minTrip.tripCost(graph)) {
                    trip.unchooseLastCity();
                    return;
                }

                // explore
                mineHelper(graph, trip, minTrip);

                // unchoose
                trip.unchooseLastCity();

            }
        }
    }

    /*
     * finds the smallest path in the DGraph object.
     * does not have pruning.
     * returns mintrip which is the smallest path in mtx
     */
    public static Trip backtrack(DGraph graph) {
        // create trip
        Trip trip = new Trip(graph.getNumNodes());
        int currentCity = 1;
        trip.chooseNextCity(currentCity);

        Trip minTrip = new Trip(graph.getNumNodes());
        backtrackHelper(graph, trip, minTrip);

        return minTrip;
    }

    /*
     * wrapper function for mine traversal.
     * traverses the DGraph object finding the smallest path possible
     */
    private static void backtrackHelper(DGraph graph, Trip trip,
            Trip minTrip) {
        if (trip.citiesLeft().size() == 0) {
            if (trip.tripCost(graph) < minTrip.tripCost(graph)) {
                minTrip.copyOtherIntoSelf(trip);
            }
            return;
        }
        if (trip.tripCost(graph) < minTrip.tripCost(graph)) {
            for (int city : trip.citiesLeft()) {
                // choose
                trip.chooseNextCity(city);

                // explore
                backtrackHelper(graph, trip, minTrip);

                // unchoose
                trip.unchooseLastCity();

            }
        }

    }

    /*
     * finds a path inside the graph which might not be the best path.
     * but it is extremely fast
     */
    public static Trip heuristic(DGraph graph) {
        // create a trip
        Trip trip = new Trip(graph.getNumNodes());

        // choose city 1 first, call it the current city
        int currentCity = 1;
        trip.chooseNextCity(currentCity);

        // iterate through the other cities
        for (int i = 2; i <= graph.getNumNodes(); i++) {
            List<Integer> neighbors = graph.getNeighbors(currentCity);
            double shortestPath = Integer.MAX_VALUE;
            int closestCity = -1;

            for (int neighbor : neighbors) {
                if (trip.isCityAvailable(neighbor)) {
                    double pathWeight = graph.getWeight(currentCity, neighbor);
                    if (pathWeight < shortestPath) {
                        closestCity = neighbor;
                        shortestPath = pathWeight;
                    }
                }
            }
            if (closestCity == -1) {
                System.out.println("Error in finding closest city.");
            }
            trip.chooseNextCity(closestCity);
            currentCity = closestCity;
        }

        return trip;
    }

    /*
     * reads through the mtx file and stores the edges
     * inside the DGraph obj
     */
    public static DGraph mtxToGraph(Scanner scan) {
        // read past all comments
        String line = null;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            if (!line.startsWith("%")) {
                break;
            }
        }
        
        // get numNodes
        String[] rowColumnEntries = line.split(" ");
        int numNodes = Integer.parseInt(rowColumnEntries[0]);
        DGraph graph = new DGraph(numNodes);

        // get edge info
        while (scan.hasNextLine()) {
            String[] edge = scan.nextLine().trim().split("\\s+");
            int v = Integer.parseInt(edge[0]);
            int w = Integer.parseInt(edge[1]);
            double weight = Double.parseDouble(edge[2]);

            graph.addEdge(v, w, weight);
        }
        return graph;
    }
}
