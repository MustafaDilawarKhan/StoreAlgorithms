# 🏪 StoreAlgorithms Setup Guide

## 📋 Prerequisites

1. **Java 8+** installed
2. **MySQL 8.0+** installed and running
3. **MySQL JDBC Driver** (mysql-connector-java-8.0.33.jar)

## 🗄️ Database Setup

### Step 1: Create Database
```bash
# Login to MySQL
mysql -u root -p

# Run the setup script
mysql -u root -p < database_setup.sql
```

### Step 2: Verify Setup
```sql
USE store_algorithms;

-- Check data counts
SELECT COUNT(*) as cities FROM cities;           -- Should show 15
SELECT COUNT(*) as products FROM products;       -- Should show 15  
SELECT COUNT(*) as warehouses FROM warehouses;   -- Should show 3
SELECT COUNT(*) as routes FROM routes;           -- Should show 25
SELECT COUNT(*) as inventory FROM warehouse_inventory; -- Should show 45

-- Test sample queries
SELECT * FROM product_inventory_summary LIMIT 5;
SELECT * FROM warehouse_details;
```

## ⚙️ Java Application Setup

### Step 1: Download JDBC Driver
```bash
# Download MySQL Connector/J
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.33.tar.gz

# Extract and copy the JAR file
tar -xzf mysql-connector-java-8.0.33.tar.gz
cp mysql-connector-java-8.0.33/mysql-connector-java-8.0.33.jar .
```

### Step 2: Update Database Credentials
Edit `src/config/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/store_algorithms";
private static final String USERNAME = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

### Step 3: Compile and Run
```bash
# Compile all Java files
javac -cp ".:mysql-connector-java-8.0.33.jar" src/**/*.java

# Run the application
java -cp ".:mysql-connector-java-8.0.33.jar:src" Main
```

## 🧪 Testing the Application

### Sample Commands to Try:
```
StoreAlgorithms> help
StoreAlgorithms> list products
StoreAlgorithms> order Laptop from Faisalabad
StoreAlgorithms> order Mobile from Peshawar
StoreAlgorithms> show route Lahore to Karachi
StoreAlgorithms> show route Islamabad to Quetta
StoreAlgorithms> clear
StoreAlgorithms> exit
```

## 📊 Database Schema Overview

### Tables Created:
- **cities** (15 Pakistani cities with coordinates)
- **products** (15 electronics products with prices)
- **warehouses** (3 warehouses in Lahore, Karachi, Islamabad)
- **routes** (25+ bidirectional routes between cities)
- **warehouse_inventory** (Product distribution across warehouses)
- **orders** (Order tracking and history)

### Key Features:
- **Realistic Pakistani geography** with actual distances
- **Distributed inventory** across multiple warehouses
- **Complete route network** for Dijkstra's algorithm
- **Sample orders** for testing
- **Performance indexes** for fast queries
- **Useful views** for reporting

## 🚀 Expected Behavior

### Order Processing:
1. User: `order Laptop from Multan`
2. System finds nearest warehouse with Laptop in stock
3. Calculates shortest route using Dijkstra's algorithm
4. Updates inventory and creates order record
5. Shows delivery details with distance and cost

### Route Finding:
1. User: `show route Lahore to Karachi`
2. System calculates shortest path: Lahore → Multan → Sukkur → Karachi
3. Shows total distance: 1200 km
4. Displays step-by-step directions

## 🔧 Troubleshooting

### Common Issues:

**Database Connection Failed:**
- Check MySQL is running: `sudo systemctl status mysql`
- Verify credentials in DBConnection.java
- Test connection: `mysql -u root -p store_algorithms`

**ClassNotFoundException:**
- Ensure JDBC driver is in classpath
- Check JAR file path: `ls -la mysql-connector-java-8.0.33.jar`

**No Products Found:**
- Verify data was inserted: `SELECT COUNT(*) FROM products;`
- Check warehouse inventory: `SELECT * FROM warehouse_inventory LIMIT 5;`

**No Route Found:**
- Check city names are exact: `SELECT name FROM cities;`
- Verify routes exist: `SELECT * FROM route_network LIMIT 10;`

## 📈 Next Steps

Once basic functionality works:
1. Add more cities and routes
2. Implement order history commands
3. Add inventory management commands
4. Create reporting features
5. Add undo/redo functionality
6. Implement order status updates

## 🎯 Sample Data Summary

### Cities: 15 Pakistani cities
- **Major**: Lahore, Karachi, Islamabad, Rawalpindi
- **Regional**: Faisalabad, Multan, Peshawar, Quetta
- **Others**: Sialkot, Gujranwala, Hyderabad, Sukkur, etc.

### Products: 15 electronics items
- **High-value**: Laptop (Rs. 120,000), Gaming Console (Rs. 85,000)
- **Mid-range**: Mobile (Rs. 40,000), Tablet (Rs. 35,000)
- **Accessories**: Headphones, Mouse, Keyboard, etc.

### Warehouses: 3 strategic locations
- **Lahore Central**: 15,000 capacity (North Punjab hub)
- **Karachi Main**: 20,000 capacity (Sindh & Balochistan)
- **Islamabad Tech**: 12,000 capacity (Federal & KPK)

Ready to test your e-commerce order fulfillment simulator! 🚀
