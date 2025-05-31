package models;

/**
 * Product Model
 * Represents a product in the e-commerce system
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private String description;

    // Default constructor
    public Product() {}

    // Parameterized constructor
    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Full constructor
    public Product(int id, String name, double price, int quantity, String category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Business methods
    public boolean isInStock() {
        return quantity > 0;
    }

    public boolean hasStock(int requiredQuantity) {
        return quantity >= requiredQuantity;
    }

    public void reduceStock(int amount) {
        if (amount <= quantity) {
            quantity -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient stock. Available: " + quantity + ", Required: " + amount);
        }
    }

    public void addStock(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f, quantity=%d, category='%s'}", 
                           id, name, price, quantity, category);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
