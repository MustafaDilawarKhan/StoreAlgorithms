package dao;

import config.DBConnection;
import models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Product Data Access Object
 * Handles all database operations for products
 */
public class ProductDAO {

    /**
     * Get all products from database
     * @return List of all products
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, quantity, category, description FROM products ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setCategory(rs.getString("category"));
                product.setDescription(rs.getString("description"));
                
                products.add(product);
            }
        }
        
        return products;
    }

    /**
     * Get product by ID
     * @param id Product ID
     * @return Product object or null if not found
     */
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT id, name, price, quantity, category, description FROM products WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setCategory(rs.getString("category"));
                    product.setDescription(rs.getString("description"));
                    
                    return product;
                }
            }
        }
        
        return null;
    }

    /**
     * Get product by name (case-insensitive)
     * @param name Product name
     * @return Product object or null if not found
     */
    public Product getProductByName(String name) throws SQLException {
        String sql = "SELECT id, name, price, quantity, category, description FROM products WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setCategory(rs.getString("category"));
                    product.setDescription(rs.getString("description"));
                    
                    return product;
                }
            }
        }
        
        return null;
    }

    /**
     * Update product quantity
     * @param productId Product ID
     * @param newQuantity New quantity
     * @return true if update successful
     */
    public boolean updateProductQuantity(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE products SET quantity = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Reduce product stock
     * @param productId Product ID
     * @param quantity Quantity to reduce
     * @return true if reduction successful
     */
    public boolean reduceStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Check if product has sufficient stock
     * @param productId Product ID
     * @param requiredQuantity Required quantity
     * @return true if sufficient stock available
     */
    public boolean hasStock(int productId, int requiredQuantity) throws SQLException {
        String sql = "SELECT quantity FROM products WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int availableQuantity = rs.getInt("quantity");
                    return availableQuantity >= requiredQuantity;
                }
            }
        }
        
        return false;
    }

    /**
     * Get products by category
     * @param category Product category
     * @return List of products in the category
     */
    public List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, quantity, category, description FROM products WHERE LOWER(category) = LOWER(?) ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setCategory(rs.getString("category"));
                    product.setDescription(rs.getString("description"));

                    products.add(product);
                }
            }
        }

        return products;
    }

    /**
     * Deep search for products using DFS-like approach
     * Searches through product hierarchy and related products
     * @param searchTerm Search term (can be partial name, category, or description)
     * @return List of products found through deep search
     */
    public List<Product> deepSearchProducts(String searchTerm) throws SQLException {
        Set<Product> foundProducts = new HashSet<>();
        Set<String> visitedCategories = new HashSet<>();

        // Start DFS from the search term
        dfsProductSearch(searchTerm, foundProducts, visitedCategories, 0, 3); // Max depth 3

        return new ArrayList<>(foundProducts);
    }

    /**
     * DFS recursive search through product categories and related items
     * @param searchTerm Current search term
     * @param foundProducts Set to store found products
     * @param visitedCategories Set to track visited categories (prevent cycles)
     * @param depth Current search depth
     * @param maxDepth Maximum search depth
     */
    private void dfsProductSearch(String searchTerm, Set<Product> foundProducts,
                                 Set<String> visitedCategories, int depth, int maxDepth) throws SQLException {
        if (depth >= maxDepth) return;

        // Search for products matching the current term
        String sql = """
            SELECT DISTINCT id, name, price, quantity, category, description
            FROM products
            WHERE LOWER(name) LIKE LOWER(?)
               OR LOWER(category) LIKE LOWER(?)
               OR LOWER(description) LIKE LOWER(?)
            ORDER BY name
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likePattern = "%" + searchTerm + "%";
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            stmt.setString(3, likePattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setCategory(rs.getString("category"));
                    product.setDescription(rs.getString("description"));

                    foundProducts.add(product);

                    // DFS: Recursively search in the same category if not visited
                    String category = product.getCategory();
                    if (category != null && !visitedCategories.contains(category.toLowerCase())) {
                        visitedCategories.add(category.toLowerCase());
                        dfsProductSearch(category, foundProducts, visitedCategories, depth + 1, maxDepth);
                    }
                }
            }
        }
    }

    /**
     * Find related products using DFS traversal through categories
     * @param productId Base product ID
     * @return List of related products
     */
    public List<Product> findRelatedProducts(int productId) throws SQLException {
        Product baseProduct = getProductById(productId);
        if (baseProduct == null || baseProduct.getCategory() == null) {
            return new ArrayList<>();
        }

        Set<Product> relatedProducts = new HashSet<>();
        Set<String> visitedCategories = new HashSet<>();

        // Start DFS from the base product's category
        dfsProductSearch(baseProduct.getCategory(), relatedProducts, visitedCategories, 0, 2);

        // Remove the original product from results
        relatedProducts.removeIf(p -> p.getId() == productId);

        return new ArrayList<>(relatedProducts);
    }
}
