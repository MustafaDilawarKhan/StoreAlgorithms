package dsa;

import java.util.*;

/**
 * Dijkstra's Algorithm Implementation
 * Finds shortest path between cities in the delivery network
 */
public class Dijkstra {

    /**
     * Node class for priority queue
     */
    private static class Node implements Comparable<Node> {
        String city;
        int distance;
        String previous;

        public Node(String city, int distance, String previous) {
            this.city = city;
            this.distance = distance;
            this.previous = previous;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    /**
     * Result class containing path and total distance
     */
    public static class PathResult {
        private List<String> path;
        private int distance;

        public PathResult(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }

        public List<String> getPath() {
            return path;
        }

        public int getDistance() {
            return distance;
        }

        public boolean isPathFound() {
            return path != null && !path.isEmpty();
        }

        @Override
        public String toString() {
            if (isPathFound()) {
                return "Path: " + String.join(" → ", path) + " (Distance: " + distance + " km)";
            } else {
                return "No path found";
            }
        }
    }

    /**
     * Find shortest path between two cities using Dijkstra's algorithm
     * @param graph The city graph
     * @param startCity Starting city
     * @param endCity Destination city
     * @return PathResult containing the shortest path and distance
     */
    public PathResult findShortestPath(Graph graph, String startCity, String endCity) {
        // Validate input
        if (!graph.hasCity(startCity) || !graph.hasCity(endCity)) {
            return new PathResult(null, -1);
        }

        if (startCity.equals(endCity)) {
            return new PathResult(Arrays.asList(startCity), 0);
        }

        // Initialize data structures
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        // Initialize distances to infinity for all cities
        for (String city : graph.getAllCities()) {
            distances.put(city, Integer.MAX_VALUE);
        }

        // Set distance to start city as 0
        distances.put(startCity, 0);
        priorityQueue.offer(new Node(startCity, 0, null));

        // Main Dijkstra's algorithm loop
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            String currentCity = current.city;

            // Skip if already visited
            if (visited.contains(currentCity)) {
                continue;
            }

            // Mark as visited
            visited.add(currentCity);

            // If we reached the destination, break
            if (currentCity.equals(endCity)) {
                break;
            }

            // Explore neighbors
            for (Graph.Edge edge : graph.getNeighbors(currentCity)) {
                String neighbor = edge.getDestination();
                int edgeWeight = edge.getWeight();

                // Skip if already visited
                if (visited.contains(neighbor)) {
                    continue;
                }

                // Calculate new distance
                int newDistance = distances.get(currentCity) + edgeWeight;

                // If we found a shorter path, update it
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentCity);
                    priorityQueue.offer(new Node(neighbor, newDistance, currentCity));
                }
            }
        }

        // Reconstruct path
        List<String> path = reconstructPath(previous, startCity, endCity);
        int totalDistance = distances.get(endCity);

        // Return result
        if (path != null && totalDistance != Integer.MAX_VALUE) {
            return new PathResult(path, totalDistance);
        } else {
            return new PathResult(null, -1);
        }
    }

    /**
     * Reconstruct the shortest path from previous pointers
     * @param previous Map of previous cities in the path
     * @param startCity Starting city
     * @param endCity Destination city
     * @return List representing the path, or null if no path exists
     */
    private List<String> reconstructPath(Map<String, String> previous, String startCity, String endCity) {
        List<String> path = new ArrayList<>();
        String current = endCity;

        // Trace back from end to start
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        // Check if we reached the start city
        if (path.get(path.size() - 1).equals(startCity)) {
            Collections.reverse(path);
            return path;
        } else {
            return null; // No path found
        }
    }

    /**
     * Find shortest distances from a source city to all other cities
     * @param graph The city graph
     * @param sourceCity Source city
     * @return Map of city names to shortest distances
     */
    public Map<String, Integer> findShortestDistances(Graph graph, String sourceCity) {
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        // Initialize distances
        for (String city : graph.getAllCities()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        distances.put(sourceCity, 0);
        priorityQueue.offer(new Node(sourceCity, 0, null));

        // Main algorithm loop
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            String currentCity = current.city;

            if (visited.contains(currentCity)) {
                continue;
            }

            visited.add(currentCity);

            // Explore neighbors
            for (Graph.Edge edge : graph.getNeighbors(currentCity)) {
                String neighbor = edge.getDestination();
                int edgeWeight = edge.getWeight();

                if (visited.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(currentCity) + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    priorityQueue.offer(new Node(neighbor, newDistance, currentCity));
                }
            }
        }

        return distances;
    }

    /**
     * Find the nearest city from a source city
     * @param graph The city graph
     * @param sourceCity Source city
     * @param targetCities List of target cities to consider
     * @return The nearest city and its distance
     */
    public PathResult findNearestCity(Graph graph, String sourceCity, List<String> targetCities) {
        Map<String, Integer> distances = findShortestDistances(graph, sourceCity);
        
        String nearestCity = null;
        int minDistance = Integer.MAX_VALUE;

        for (String city : targetCities) {
            if (distances.containsKey(city) && distances.get(city) < minDistance) {
                minDistance = distances.get(city);
                nearestCity = city;
            }
        }

        if (nearestCity != null) {
            PathResult fullPath = findShortestPath(graph, sourceCity, nearestCity);
            return fullPath;
        } else {
            return new PathResult(null, -1);
        }
    }
}
