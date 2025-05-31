package dao;

import config.DBConnection;
import models.City;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * City Data Access Object
 * Handles database operations for cities
 */
public class CityDAO {

    /**
     * Get all cities from database
     * @return List of all cities
     */
    public List<City> getAllCities() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT id, name, province, latitude, longitude, population FROM cities ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                City city = new City();
                city.setId(rs.getInt("id"));
                city.setName(rs.getString("name"));
                city.setProvince(rs.getString("province"));
                city.setLatitude(rs.getDouble("latitude"));
                city.setLongitude(rs.getDouble("longitude"));
                city.setPopulation(rs.getInt("population"));
                
                cities.add(city);
            }
        }
        
        return cities;
    }

    /**
     * Get city by ID
     * @param id City ID
     * @return City object or null if not found
     */
    public City getCityById(int id) throws SQLException {
        String sql = "SELECT id, name, province, latitude, longitude, population FROM cities WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = new City();
                    city.setId(rs.getInt("id"));
                    city.setName(rs.getString("name"));
                    city.setProvince(rs.getString("province"));
                    city.setLatitude(rs.getDouble("latitude"));
                    city.setLongitude(rs.getDouble("longitude"));
                    city.setPopulation(rs.getInt("population"));
                    
                    return city;
                }
            }
        }
        
        return null;
    }

    /**
     * Get city by name (case-insensitive)
     * @param name City name
     * @return City object or null if not found
     */
    public City getCityByName(String name) throws SQLException {
        String sql = "SELECT id, name, province, latitude, longitude, population FROM cities WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    City city = new City();
                    city.setId(rs.getInt("id"));
                    city.setName(rs.getString("name"));
                    city.setProvince(rs.getString("province"));
                    city.setLatitude(rs.getDouble("latitude"));
                    city.setLongitude(rs.getDouble("longitude"));
                    city.setPopulation(rs.getInt("population"));
                    
                    return city;
                }
            }
        }
        
        return null;
    }

    /**
     * Get cities by province
     * @param province Province name
     * @return List of cities in the province
     */
    public List<City> getCitiesByProvince(String province) throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT id, name, province, latitude, longitude, population FROM cities WHERE LOWER(province) = LOWER(?) ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, province);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    City city = new City();
                    city.setId(rs.getInt("id"));
                    city.setName(rs.getString("name"));
                    city.setProvince(rs.getString("province"));
                    city.setLatitude(rs.getDouble("latitude"));
                    city.setLongitude(rs.getDouble("longitude"));
                    city.setPopulation(rs.getInt("population"));
                    
                    cities.add(city);
                }
            }
        }
        
        return cities;
    }

    /**
     * Check if city exists
     * @param cityName City name
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
     * Get metro cities (population > 1M)
     * @return List of metro cities
     */
    public List<City> getMetroCities() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT id, name, province, latitude, longitude, population FROM cities WHERE population > 1000000 ORDER BY population DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                City city = new City();
                city.setId(rs.getInt("id"));
                city.setName(rs.getString("name"));
                city.setProvince(rs.getString("province"));
                city.setLatitude(rs.getDouble("latitude"));
                city.setLongitude(rs.getDouble("longitude"));
                city.setPopulation(rs.getInt("population"));
                
                cities.add(city);
            }
        }
        
        return cities;
    }
}
