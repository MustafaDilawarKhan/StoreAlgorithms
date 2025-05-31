package commands;

import dsa.Dijkstra;
import dsa.Graph;
import dao.RouteDAO;
import utils.Printer;
import java.util.List;

/**
 * Route Command
 * Displays shortest route between two cities using Dijkstra's algorithm
 */
public class RouteCommand {
    private final RouteDAO routeDAO;
    private final Graph cityGraph;
    private final Dijkstra dijkstra;

    public RouteCommand() {
        this.routeDAO = new RouteDAO();
        this.cityGraph = new Graph();
        this.dijkstra = new Dijkstra();
        
        // Load city graph from database
        loadCityGraph();
    }

    public void execute(String[] args) {
        if (args.length < 2) {
            Printer.printError("Invalid route format!");
            System.out.println("💡 Usage: " + Printer.CYAN + "show route <city1> to <city2>" + Printer.RESET);
            System.out.println("📝 Example: " + Printer.GREEN + "show route Lahore to Karachi" + Printer.RESET);
            return;
        }

        String fromCity = args[0];
        String toCity = args[1];

        try {
            Printer.printInfo("Finding shortest route from " + fromCity + " to " + toCity + "...");
            
            // Find shortest path using Dijkstra's algorithm
            Dijkstra.PathResult result = dijkstra.findShortestPath(cityGraph, fromCity, toCity);
            
            if (result != null && result.getPath() != null && !result.getPath().isEmpty()) {
                displayRoute(result, fromCity, toCity);
            } else {
                handleRouteNotFound(fromCity, toCity);
            }
            
        } catch (Exception e) {
            Printer.printError("Route calculation failed: " + e.getMessage());
            System.out.println("🔧 Please check city names and try again.");
        }
    }

    private void displayRoute(Dijkstra.PathResult result, String fromCity, String toCity) {
        Printer.printSeparator();
        Printer.printSuccess("Shortest route found!");
        
        System.out.println();
        System.out.println(Printer.BLUE + "🗺️  Route Details:" + Printer.RESET);
        
        List<String> path = result.getPath();
        int totalDistance = result.getDistance();
        
        // Display the route path
        StringBuilder routeBuilder = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            routeBuilder.append(path.get(i));
            if (i < path.size() - 1) {
                routeBuilder.append(" → ");
            }
        }
        
        Printer.printRoute(routeBuilder.toString(), totalDistance);
        
        System.out.println();
        System.out.println(Printer.GREEN + "📊 Route Statistics:" + Printer.RESET);
        System.out.println("  🏁 Starting City: " + fromCity);
        System.out.println("  🎯 Destination: " + toCity);
        System.out.println("  🛣️  Total Cities: " + path.size());
        System.out.println("  📏 Total Distance: " + totalDistance + " km");
        System.out.println("  🚚 Estimated Delivery Cost: Rs. " + (totalDistance * 10));
        
        // Display step-by-step directions
        if (path.size() > 2) {
            System.out.println();
            System.out.println(Printer.YELLOW + "🧭 Step-by-step Directions:" + Printer.RESET);
            
            for (int i = 0; i < path.size() - 1; i++) {
                String currentCity = path.get(i);
                String nextCity = path.get(i + 1);
                
                // Get distance between consecutive cities
                int segmentDistance = getDistanceBetweenCities(currentCity, nextCity);
                
                System.out.printf("  %d. %s → %s (%d km)%n", 
                                (i + 1), currentCity, nextCity, segmentDistance);
            }
        }
        
        Printer.printSeparator();
        System.out.println("💡 This route is optimized for minimum distance using Dijkstra's algorithm!");
    }

    private void handleRouteNotFound(String fromCity, String toCity) {
        Printer.printError("No route found between " + fromCity + " and " + toCity + "!");
        
        System.out.println();
        System.out.println("❌ Possible reasons:");
        System.out.println("  • One or both cities are not in our delivery network");
        System.out.println("  • Cities are not connected by available routes");
        System.out.println("  • City names may be misspelled");
        
        System.out.println();
        System.out.println("💡 Available cities in our network:");
        
        // Display available cities (first few)
        try {
            List<String> availableCities = routeDAO.getAllCityNames();
            int displayCount = Math.min(availableCities.size(), 8);
            
            for (int i = 0; i < displayCount; i++) {
                System.out.println("  • " + availableCities.get(i));
            }
            
            if (availableCities.size() > displayCount) {
                System.out.println("  ... and " + (availableCities.size() - displayCount) + " more cities");
            }
            
        } catch (Exception e) {
            System.out.println("  (Unable to load city list)");
        }
    }

    private void loadCityGraph() {
        try {
            routeDAO.loadGraphFromDatabase(cityGraph);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load city graph from database: " + e.getMessage());
        }
    }

    private int getDistanceBetweenCities(String city1, String city2) {
        try {
            return routeDAO.getDirectDistance(city1, city2);
        } catch (Exception e) {
            return 0; // Return 0 if distance cannot be determined
        }
    }
}
