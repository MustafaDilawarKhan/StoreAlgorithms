package dao;

import config.DBConnection;
import dsa.Dijkstra;
import dsa.Graph;
import models.Order;
import models.Product;
import models.Warehouse;
import utils.Printer;

import java.sql.*;
import java.util.*;

/**
 * Order Processor
 * Handles order fulfillment logic with shortest path warehouse selection
 */
public class OrderProcessor {
    private final ProductDAO productDAO;
    private final WarehouseDAO warehouseDAO;
    private final RouteDAO routeDAO;
    private final Graph cityGraph;
    private final Dijkstra dijkstra;

    public OrderProcessor() {
        this.productDAO = new ProductDAO();
        this.warehouseDAO = new WarehouseDAO();
        this.routeDAO = new RouteDAO();
        this.cityGraph = new Graph();
        this.dijkstra = new Dijkstra();
        
        // Load city graph for route calculations
        loadCityGraph();
    }

    /**
     * Process a customer order
     * @param productName Product name
     * @param customerCity Customer's city
     * @param quantity Quantity ordered
     * @return Order object if successful, null otherwise
     */
    public Order processOrder(String productName, String customerCity, int quantity) {
        return processOrder(productName, customerCity, quantity, false); // Default to Dijkstra
    }

    /**
     * Process a customer order with algorithm choice
     * @param productName Product name
     * @param customerCity Customer's city
     * @param quantity Quantity ordered
     * @param fastDelivery If true, use BFS for fastest delivery; if false, use Dijkstra for shortest distance
     * @return Order object if successful, null otherwise
     */
    public Order processOrder(String productName, String customerCity, int quantity, boolean fastDelivery) {
        try {
            // Step 1: Find the product
            Product product = productDAO.getProductByName(productName);
            if (product == null) {
                Printer.printError("Product '" + productName + "' not found!");
                return null;
            }

            // Step 2: Check if customer city exists
            if (!routeDAO.cityExists(customerCity)) {
                Printer.printError("City '" + customerCity + "' is not in our delivery network!");
                return null;
            }

            // Step 3: Find warehouses with the product in stock
            List<Warehouse> availableWarehouses = warehouseDAO.getWarehousesWithProduct(product.getId(), quantity);
            if (availableWarehouses.isEmpty()) {
                Printer.printError("Product '" + productName + "' is out of stock in all warehouses!");
                return null;
            }

            // Step 4: Find the best warehouse using chosen algorithm
            Warehouse selectedWarehouse;
            int deliveryDistance;

            if (fastDelivery) {
                // Use BFS for fastest delivery (minimum hops)
                selectedWarehouse = findFastestWarehouse(customerCity, availableWarehouses);
                if (selectedWarehouse == null) {
                    Printer.printError("No reachable warehouse found for fast delivery to " + customerCity);
                    return null;
                }

                // Calculate actual distance using Dijkstra (for cost calculation)
                Dijkstra.PathResult pathResult = dijkstra.findShortestPath(cityGraph, customerCity, selectedWarehouse.getCityName());
                if (pathResult == null || !pathResult.isPathFound()) {
                    Printer.printError("No delivery route found to " + customerCity);
                    return null;
                }
                deliveryDistance = pathResult.getDistance();

            } else {
                // Use Dijkstra for shortest distance (lowest cost)
                selectedWarehouse = findNearestWarehouse(customerCity, availableWarehouses);
                if (selectedWarehouse == null) {
                    Printer.printError("No reachable warehouse found for delivery to " + customerCity);
                    return null;
                }

                // Calculate distance using Dijkstra result
                Dijkstra.PathResult pathResult = dijkstra.findShortestPath(cityGraph, customerCity, selectedWarehouse.getCityName());
                if (pathResult == null || !pathResult.isPathFound()) {
                    Printer.printError("No delivery route found to " + customerCity);
                    return null;
                }
                deliveryDistance = pathResult.getDistance();
            }

            // Step 6: Create and save the order
            Order order = createOrder(product, customerCity, quantity, selectedWarehouse, deliveryDistance);

            // Step 7: Update warehouse inventory
            if (warehouseDAO.reduceInventory(selectedWarehouse.getId(), product.getId(), quantity)) {
                order.setStatus(Order.OrderStatus.CONFIRMED);
                saveOrder(order);
                return order;
            } else {
                Printer.printError("Failed to update warehouse inventory!");
                return null;
            }

        } catch (Exception e) {
            Printer.printError("Order processing failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find the nearest warehouse to customer city using Dijkstra (shortest distance)
     * @param customerCity Customer's city
     * @param warehouses List of available warehouses
     * @return Nearest warehouse or null if none reachable
     */
    private Warehouse findNearestWarehouse(String customerCity, List<Warehouse> warehouses) {
        Warehouse nearestWarehouse = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            try {
                Dijkstra.PathResult pathResult = dijkstra.findShortestPath(cityGraph, customerCity, warehouse.getCityName());

                if (pathResult != null && pathResult.isPathFound() && pathResult.getDistance() < shortestDistance) {
                    shortestDistance = pathResult.getDistance();
                    nearestWarehouse = warehouse;
                }
            } catch (Exception e) {
                // Skip this warehouse if route calculation fails
                continue;
            }
        }

        return nearestWarehouse;
    }

    /**
     * Find the fastest warehouse to customer city using BFS (minimum hops)
     * Used when customer prioritizes delivery speed over cost
     * @param customerCity Customer's city
     * @param warehouses List of available warehouses
     * @return Fastest warehouse or null if none reachable
     */
    private Warehouse findFastestWarehouse(String customerCity, List<Warehouse> warehouses) {
        Warehouse fastestWarehouse = null;
        int minimumHops = Integer.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            try {
                int hops = bfsMinimumHops(customerCity, warehouse.getCityName());

                if (hops != -1 && hops < minimumHops) {
                    minimumHops = hops;
                    fastestWarehouse = warehouse;
                }
            } catch (Exception e) {
                // Skip this warehouse if route calculation fails
                continue;
            }
        }

        return fastestWarehouse;
    }

    /**
     * Simple BFS to find minimum hops between two cities
     * @param startCity Starting city
     * @param endCity Destination city
     * @return Number of hops, or -1 if no path found
     */
    private int bfsMinimumHops(String startCity, String endCity) {
        if (startCity.equals(endCity)) return 0;

        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> hopCount = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(startCity);
        visited.add(startCity);
        hopCount.put(startCity, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentHops = hopCount.get(current);

            for (Graph.Edge edge : cityGraph.getNeighbors(current)) {
                String neighbor = edge.getDestination();

                if (neighbor.equals(endCity)) {
                    return currentHops + 1; // Found destination
                }

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    hopCount.put(neighbor, currentHops + 1);
                    queue.offer(neighbor);
                }
            }
        }

        return -1; // No path found
    }

    /**
     * Create an order object
     * @param product Product being ordered
     * @param customerCity Customer's city
     * @param quantity Quantity ordered
     * @param warehouse Selected warehouse
     * @param deliveryDistance Delivery distance in km
     * @return Order object
     */
    private Order createOrder(Product product, String customerCity, int quantity, Warehouse warehouse, int deliveryDistance) {
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        order.setCustomerCity(customerCity);
        order.setWarehouseId(warehouse.getId());
        order.setWarehouseName(warehouse.getName());
        order.setWarehouseCity(warehouse.getCityName());
        order.setDeliveryDistance(deliveryDistance);
        order.setStatus(Order.OrderStatus.PENDING);
        
        return order;
    }

    /**
     * Save order to database
     * @param order Order to save
     * @return Generated order ID
     */
    private int saveOrder(Order order) throws SQLException {
        String sql = """
            INSERT INTO orders (product_id, product_name, quantity, total_price, customer_city, 
                               warehouse_id, warehouse_name, warehouse_city, delivery_distance, status, order_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, order.getProductId());
            stmt.setString(2, order.getProductName());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setString(5, order.getCustomerCity());
            stmt.setInt(6, order.getWarehouseId());
            stmt.setString(7, order.getWarehouseName());
            stmt.setString(8, order.getWarehouseCity());
            stmt.setInt(9, order.getDeliveryDistance());
            stmt.setString(10, order.getStatus().toString());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setId(orderId);
                        return orderId;
                    }
                }
            }
        }
        
        return -1;
    }

    /**
     * Load city graph from database
     */
    private void loadCityGraph() {
        try {
            routeDAO.loadGraphFromDatabase(cityGraph);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load city graph: " + e.getMessage());
        }
    }

    /**
     * Get order by ID
     * @param orderId Order ID
     * @return Order object or null if not found
     */
    public Order getOrderById(int orderId) throws SQLException {
        String sql = """
            SELECT id, product_id, product_name, quantity, total_price, customer_city,
                   warehouse_id, warehouse_name, warehouse_city, delivery_distance, status, order_date
            FROM orders WHERE id = ?
            """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setProductId(rs.getInt("product_id"));
                    order.setProductName(rs.getString("product_name"));
                    order.setQuantity(rs.getInt("quantity"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setCustomerCity(rs.getString("customer_city"));
                    order.setWarehouseId(rs.getInt("warehouse_id"));
                    order.setWarehouseName(rs.getString("warehouse_name"));
                    order.setWarehouseCity(rs.getString("warehouse_city"));
                    order.setDeliveryDistance(rs.getInt("delivery_distance"));
                    order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    
                    return order;
                }
            }
        }
        
        return null;
    }
}
