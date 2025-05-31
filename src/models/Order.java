package models;

import java.time.LocalDateTime;

/**
 * Order Model
 * Represents a customer order in the system
 */
public class Order {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private double totalPrice;
    private String customerCity;
    private int warehouseId;
    private String warehouseName;
    private String warehouseCity;
    private int deliveryDistance;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    // Order Status Enum
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    // Default constructor
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Basic constructor
    public Order(int productId, String productName, int quantity, double totalPrice, String customerCity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.customerCity = customerCity;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }
    
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    
    public String getWarehouseCity() { return warehouseCity; }
    public void setWarehouseCity(String warehouseCity) { this.warehouseCity = warehouseCity; }
    
    public int getDeliveryDistance() { return deliveryDistance; }
    public void setDeliveryDistance(int deliveryDistance) { this.deliveryDistance = deliveryDistance; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    // Business methods
    public void confirmOrder(int warehouseId, String warehouseName, String warehouseCity, int distance) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.warehouseCity = warehouseCity;
        this.deliveryDistance = distance;
        this.status = OrderStatus.CONFIRMED;
    }

    public double getDeliveryCost() {
        return deliveryDistance * 10.0; // Rs. 10 per km
    }

    public double getFinalTotal() {
        return totalPrice + getDeliveryCost();
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, product='%s', qty=%d, total=Rs.%.2f, from='%s', to='%s', status=%s}", 
                           id, productName, quantity, totalPrice, warehouseCity, customerCity, status);
    }
}
