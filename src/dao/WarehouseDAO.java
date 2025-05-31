package dao;

import config.DBConnection;
import models.Warehouse;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Warehouse Data Access Object
 * Handles database operations for warehouses and inventory
 */
public class WarehouseDAO {

    /**
     * Get all warehouses
     * @return List of all warehouses
     */
    public List<Warehouse> getAllWarehouses() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = """
            SELECT w.id, w.name, w.city_id, c.name as city_name, w.address, w.capacity
            FROM warehouses w
            JOIN cities c ON w.city_id = c.id
            ORDER BY w.name
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Warehouse warehouse = new Warehouse();
                warehouse.setId(rs.getInt("id"));
                warehouse.setName(rs.getString("name"));
                warehouse.setCityId(rs.getInt("city_id"));
                warehouse.setCityName(rs.getString("city_name"));
                warehouse.setAddress(rs.getString("address"));
                warehouse.setCapacity(rs.getInt("capacity"));
                
                warehouses.add(warehouse);
            }
        }

        // Load inventory for all warehouses after the main query
        for (Warehouse warehouse : warehouses) {
            loadWarehouseInventory(warehouse);
        }

        return warehouses;
    }

    /**
     * Get warehouse by ID
     * @param id Warehouse ID
     * @return Warehouse object or null if not found
     */
    public Warehouse getWarehouseById(int id) throws SQLException {
        String sql = """
            SELECT w.id, w.name, w.city_id, c.name as city_name, w.address, w.capacity
            FROM warehouses w
            JOIN cities c ON w.city_id = c.id
            WHERE w.id = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setId(rs.getInt("id"));
                    warehouse.setName(rs.getString("name"));
                    warehouse.setCityId(rs.getInt("city_id"));
                    warehouse.setCityName(rs.getString("city_name"));
                    warehouse.setAddress(rs.getString("address"));
                    warehouse.setCapacity(rs.getInt("capacity"));
                    
                    // Load inventory for this warehouse
                    loadWarehouseInventory(warehouse);

                    return warehouse;
                }
            }
        }
        
        return null;
    }

    /**
     * Get warehouses that have a specific product in stock
     * @param productId Product ID
     * @param requiredQuantity Required quantity
     * @return List of warehouses with sufficient stock
     */
    public List<Warehouse> getWarehousesWithProduct(int productId, int requiredQuantity) throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = """
            SELECT w.id, w.name, w.city_id, c.name as city_name, w.address, w.capacity, wi.quantity
            FROM warehouses w
            JOIN cities c ON w.city_id = c.id
            JOIN warehouse_inventory wi ON w.id = wi.warehouse_id
            WHERE wi.product_id = ? AND wi.quantity >= ?
            ORDER BY wi.quantity DESC
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            stmt.setInt(2, requiredQuantity);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setId(rs.getInt("id"));
                    warehouse.setName(rs.getString("name"));
                    warehouse.setCityId(rs.getInt("city_id"));
                    warehouse.setCityName(rs.getString("city_name"));
                    warehouse.setAddress(rs.getString("address"));
                    warehouse.setCapacity(rs.getInt("capacity"));
                    
                    warehouses.add(warehouse);
                }
            }
        }

        // Load inventory for all warehouses after the main query
        for (Warehouse warehouse : warehouses) {
            loadWarehouseInventory(warehouse);
        }

        return warehouses;
    }

    /**
     * Load inventory for a specific warehouse
     * @param warehouse Warehouse object to populate with inventory
     */
    private void loadWarehouseInventory(Warehouse warehouse) throws SQLException {
        String sql = "SELECT product_id, quantity FROM warehouse_inventory WHERE warehouse_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, warehouse.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                Map<Integer, Integer> inventory = new HashMap<>();
                
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");
                    inventory.put(productId, quantity);
                }
                
                warehouse.setInventory(inventory);
            }
        }
    }

    /**
     * Reduce inventory for a product in a warehouse
     * @param warehouseId Warehouse ID
     * @param productId Product ID
     * @param quantity Quantity to reduce
     * @return true if reduction successful
     */
    public boolean reduceInventory(int warehouseId, int productId, int quantity) throws SQLException {
        String sql = """
            UPDATE warehouse_inventory 
            SET quantity = quantity - ? 
            WHERE warehouse_id = ? AND product_id = ? AND quantity >= ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, warehouseId);
            stmt.setInt(3, productId);
            stmt.setInt(4, quantity);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Add inventory for a product in a warehouse
     * @param warehouseId Warehouse ID
     * @param productId Product ID
     * @param quantity Quantity to add
     * @return true if addition successful
     */
    public boolean addInventory(int warehouseId, int productId, int quantity) throws SQLException {
        String sql = """
            INSERT INTO warehouse_inventory (warehouse_id, product_id, quantity)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = quantity + ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Get inventory quantity for a specific product in a warehouse
     * @param warehouseId Warehouse ID
     * @param productId Product ID
     * @return Quantity available, or 0 if not found
     */
    public int getInventoryQuantity(int warehouseId, int productId) throws SQLException {
        String sql = "SELECT quantity FROM warehouse_inventory WHERE warehouse_id = ? AND product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        
        return 0;
    }

    /**
     * Get warehouses in a specific city
     * @param cityName City name
     * @return List of warehouses in the city
     */
    public List<Warehouse> getWarehousesByCity(String cityName) throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = """
            SELECT w.id, w.name, w.city_id, c.name as city_name, w.address, w.capacity
            FROM warehouses w
            JOIN cities c ON w.city_id = c.id
            WHERE LOWER(c.name) = LOWER(?)
            ORDER BY w.name
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cityName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setId(rs.getInt("id"));
                    warehouse.setName(rs.getString("name"));
                    warehouse.setCityId(rs.getInt("city_id"));
                    warehouse.setCityName(rs.getString("city_name"));
                    warehouse.setAddress(rs.getString("address"));
                    warehouse.setCapacity(rs.getInt("capacity"));
                    
                    warehouses.add(warehouse);
                }
            }
        }

        // Load inventory for all warehouses after the main query
        for (Warehouse warehouse : warehouses) {
            loadWarehouseInventory(warehouse);
        }

        return warehouses;
    }
}
