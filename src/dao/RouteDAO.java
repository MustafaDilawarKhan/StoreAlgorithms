package dao;

import config.DBConnection;
import dsa.Graph;
import models.Route;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Route Data Access Object
 * Handles database operations for city routes and graph loading
 */
public class RouteDAO {

    /**
     * Load the complete city graph from database
     * @param graph Graph object to populate
     */
    public void loadGraphFromDatabase(Graph graph) throws SQLException {
        String sql = """
            SELECT r.from_city_id, c1.name as from_city, r.to_city_id, c2.name as to_city, r.distance
            FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String fromCity = rs.getString("from_city");
                String toCity = rs.getString("to_city");
                int distance = rs.getInt("distance");
                
                // Add bidirectional route to graph
                graph.addRoute(fromCity, toCity, distance);
            }
        }
    }

    /**
     * Get all route records from database
     * @return List of Route objects
     */
    public List<Route> getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = """
            SELECT r.id, r.from_city_id, c1.name as from_city, r.to_city_id, c2.name as to_city, 
                   r.distance, r.road_type
            FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            ORDER BY c1.name, c2.name
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Route route = new Route();
                route.setId(rs.getInt("id"));
                route.setFromCityId(rs.getInt("from_city_id"));
                route.setFromCityName(rs.getString("from_city"));
                route.setToCityId(rs.getInt("to_city_id"));
                route.setToCityName(rs.getString("to_city"));
                route.setDistance(rs.getInt("distance"));
                route.setRoadType(rs.getString("road_type"));
                route.setBidirectional(true); // Assume bidirectional by default
                
                routes.add(route);
            }
        }
        
        return routes;
    }

    /**
     * Get direct distance between two cities
     * @param city1 First city name
     * @param city2 Second city name
     * @return Distance in km, or -1 if no direct route
     */
    public int getDirectDistance(String city1, String city2) throws SQLException {
        String sql = """
            SELECT r.distance
            FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            WHERE (LOWER(c1.name) = LOWER(?) AND LOWER(c2.name) = LOWER(?))
               OR (LOWER(c1.name) = LOWER(?) AND LOWER(c2.name) = LOWER(?))
            LIMIT 1
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city1);
            stmt.setString(2, city2);
            stmt.setString(3, city2);
            stmt.setString(4, city1);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("distance");
                }
            }
        }
        
        return -1; // No direct route found
    }

    /**
     * Get all city names from the database
     * @return List of city names
     */
    public List<String> getAllCityNames() throws SQLException {
        List<String> cityNames = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM cities ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cityNames.add(rs.getString("name"));
            }
        }
        
        return cityNames;
    }

    /**
     * Check if a city exists in the database
     * @param cityName City name to check
     * @return true if city exists
     */
    public boolean cityExists(String cityName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cities WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cityName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    /**
     * Get city ID by name
     * @param cityName City name
     * @return City ID or -1 if not found
     */
    public int getCityId(String cityName) throws SQLException {
        String sql = "SELECT id FROM cities WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cityName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        
        return -1; // City not found
    }

    /**
     * Get routes from a specific city
     * @param cityName Source city name
     * @return List of routes from the city
     */
    public List<Route> getRoutesFromCity(String cityName) throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = """
            SELECT r.id, r.from_city_id, c1.name as from_city, r.to_city_id, c2.name as to_city, 
                   r.distance, r.road_type
            FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            WHERE LOWER(c1.name) = LOWER(?)
            ORDER BY r.distance
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cityName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Route route = new Route();
                    route.setId(rs.getInt("id"));
                    route.setFromCityId(rs.getInt("from_city_id"));
                    route.setFromCityName(rs.getString("from_city"));
                    route.setToCityId(rs.getInt("to_city_id"));
                    route.setToCityName(rs.getString("to_city"));
                    route.setDistance(rs.getInt("distance"));
                    route.setRoadType(rs.getString("road_type"));
                    route.setBidirectional(true);
                    
                    routes.add(route);
                }
            }
        }
        
        return routes;
    }
}
