package dsa;

import java.util.*;

/**
 * Graph Data Structure
 * Represents cities and routes using adjacency list
 */
public class Graph {
    private Map<String, List<Edge>> adjacencyList;
    private Set<String> cities;

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.cities = new HashSet<>();
    }

    /**
     * Edge class representing a route between cities
     */
    public static class Edge {
        private String destination;
        private int weight;

        public Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        public String getDestination() {
            return destination;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return destination + "(" + weight + "km)";
        }
    }

    /**
     * Add a city to the graph
     * @param city City name
     */
    public void addCity(String city) {
        cities.add(city);
        adjacencyList.putIfAbsent(city, new ArrayList<>());
    }

    /**
     * Add a bidirectional route between two cities
     * @param city1 First city
     * @param city2 Second city
     * @param distance Distance in kilometers
     */
    public void addRoute(String city1, String city2, int distance) {
        addCity(city1);
        addCity(city2);
        
        // Add bidirectional edges
        adjacencyList.get(city1).add(new Edge(city2, distance));
        adjacencyList.get(city2).add(new Edge(city1, distance));
    }

    /**
     * Add a unidirectional route between two cities
     * @param fromCity Source city
     * @param toCity Destination city
     * @param distance Distance in kilometers
     */
    public void addDirectedRoute(String fromCity, String toCity, int distance) {
        addCity(fromCity);
        addCity(toCity);
        
        adjacencyList.get(fromCity).add(new Edge(toCity, distance));
    }

    /**
     * Get all neighbors of a city
     * @param city City name
     * @return List of edges (neighboring cities with distances)
     */
    public List<Edge> getNeighbors(String city) {
        return adjacencyList.getOrDefault(city, new ArrayList<>());
    }

    /**
     * Check if a city exists in the graph
     * @param city City name
     * @return true if city exists
     */
    public boolean hasCity(String city) {
        return cities.contains(city);
    }

    /**
     * Get all cities in the graph
     * @return Set of all city names
     */
    public Set<String> getAllCities() {
        return new HashSet<>(cities);
    }

    /**
     * Get the number of cities in the graph
     * @return Number of cities
     */
    public int getCityCount() {
        return cities.size();
    }

    /**
     * Get the total number of routes in the graph
     * @return Number of routes
     */
    public int getRouteCount() {
        return adjacencyList.values().stream()
                .mapToInt(List::size)
                .sum() / 2; // Divide by 2 for bidirectional routes
    }

    /**
     * Get direct distance between two cities
     * @param city1 First city
     * @param city2 Second city
     * @return Distance if direct route exists, -1 otherwise
     */
    public int getDirectDistance(String city1, String city2) {
        List<Edge> neighbors = getNeighbors(city1);
        for (Edge edge : neighbors) {
            if (edge.getDestination().equals(city2)) {
                return edge.getWeight();
            }
        }
        return -1; // No direct route
    }

    /**
     * Check if two cities are directly connected
     * @param city1 First city
     * @param city2 Second city
     * @return true if directly connected
     */
    public boolean areDirectlyConnected(String city1, String city2) {
        return getDirectDistance(city1, city2) != -1;
    }

    /**
     * Clear all data from the graph
     */
    public void clear() {
        adjacencyList.clear();
        cities.clear();
    }

    /**
     * Display graph information
     */
    public void printGraph() {
        System.out.println("Graph Information:");
        System.out.println("Cities: " + getCityCount());
        System.out.println("Routes: " + getRouteCount());
        System.out.println();
        
        for (String city : cities) {
            System.out.print(city + " -> ");
            List<Edge> neighbors = getNeighbors(city);
            for (int i = 0; i < neighbors.size(); i++) {
                System.out.print(neighbors.get(i));
                if (i < neighbors.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }















    @Override
    public String toString() {
        return "Graph{cities=" + getCityCount() + ", routes=" + getRouteCount() + "}";
    }
}
