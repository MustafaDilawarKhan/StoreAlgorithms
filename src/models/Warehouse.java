package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Warehouse Model
 * Represents a warehouse in the delivery network
 */
public class Warehouse {
    private int id;
    private String name;
    private int cityId;
    private String cityName;
    private String address;
    private int capacity;
    private Map<Integer, Integer> inventory; // productId -> quantity

    // Default constructor
    public Warehouse() {
        this.inventory = new HashMap<>();
    }

    // Basic constructor
    public Warehouse(int id, String name, int cityId) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.inventory = new HashMap<>();
    }

    // Constructor with city name
    public Warehouse(int id, String name, int cityId, String cityName) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.cityName = cityName;
        this.inventory = new HashMap<>();
    }

    // Full constructor
    public Warehouse(int id, String name, int cityId, String cityName, String address, int capacity) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.cityName = cityName;
        this.address = address;
        this.capacity = capacity;
        this.inventory = new HashMap<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Map<Integer, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<Integer, Integer> inventory) {
        this.inventory = inventory;
    }

    // Business methods
    public boolean hasProduct(int productId) {
        return inventory.containsKey(productId) && inventory.get(productId) > 0;
    }

    public boolean hasStock(int productId, int requiredQuantity) {
        return inventory.containsKey(productId) && inventory.get(productId) >= requiredQuantity;
    }

    public int getProductQuantity(int productId) {
        return inventory.getOrDefault(productId, 0);
    }

    public void addProduct(int productId, int quantity) {
        inventory.put(productId, inventory.getOrDefault(productId, 0) + quantity);
    }

    public boolean removeProduct(int productId, int quantity) {
        if (hasStock(productId, quantity)) {
            int currentQuantity = inventory.get(productId);
            if (currentQuantity == quantity) {
                inventory.remove(productId);
            } else {
                inventory.put(productId, currentQuantity - quantity);
            }
            return true;
        }
        return false;
    }

    public int getTotalProducts() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isAtCapacity() {
        return getTotalProducts() >= capacity;
    }

    @Override
    public String toString() {
        return String.format("Warehouse{id=%d, name='%s', city='%s', products=%d}", 
                           id, name, cityName, inventory.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Warehouse warehouse = (Warehouse) obj;
        return id == warehouse.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
