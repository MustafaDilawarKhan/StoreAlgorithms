-- =====================================================
-- StoreAlgorithms Database Test Script
-- Run this to verify your database setup
-- =====================================================

USE store_algorithms;

-- =====================================================
-- 1. BASIC DATA VERIFICATION
-- =====================================================
SELECT '=== DATA COUNTS ===' as test_section;

SELECT 
    (SELECT COUNT(*) FROM cities) as cities_count,
    (SELECT COUNT(*) FROM products) as products_count,
    (SELECT COUNT(*) FROM warehouses) as warehouses_count,
    (SELECT COUNT(*) FROM routes) as routes_count,
    (SELECT COUNT(*) FROM warehouse_inventory) as inventory_records,
    (SELECT COUNT(*) FROM orders) as sample_orders;

-- =====================================================
-- 2. SAMPLE PRODUCTS WITH INVENTORY
-- =====================================================
SELECT '=== PRODUCTS WITH TOTAL INVENTORY ===' as test_section;

SELECT 
    p.name,
    p.price,
    SUM(wi.quantity) as total_stock,
    COUNT(wi.warehouse_id) as warehouses_with_stock
FROM products p
LEFT JOIN warehouse_inventory wi ON p.id = wi.product_id
GROUP BY p.id, p.name, p.price
ORDER BY total_stock DESC
LIMIT 10;

-- =====================================================
-- 3. WAREHOUSE DETAILS
-- =====================================================
SELECT '=== WAREHOUSE INFORMATION ===' as test_section;

SELECT 
    w.name as warehouse_name,
    c.name as city,
    c.province,
    COUNT(wi.product_id) as product_types,
    SUM(wi.quantity) as total_items
FROM warehouses w
JOIN cities c ON w.city_id = c.id
LEFT JOIN warehouse_inventory wi ON w.id = wi.warehouse_id
GROUP BY w.id, w.name, c.name, c.province;

-- =====================================================
-- 4. ROUTE NETWORK SAMPLE
-- =====================================================
SELECT '=== SAMPLE ROUTES FROM LAHORE ===' as test_section;

SELECT 
    c1.name as from_city,
    c2.name as to_city,
    r.distance as distance_km,
    r.road_type
FROM routes r
JOIN cities c1 ON r.from_city_id = c1.id
JOIN cities c2 ON r.to_city_id = c2.id
WHERE c1.name = 'Lahore'
ORDER BY r.distance;

-- =====================================================
-- 5. INVENTORY BY WAREHOUSE
-- =====================================================
SELECT '=== LAHORE WAREHOUSE INVENTORY ===' as test_section;

SELECT 
    p.name as product,
    p.price,
    wi.quantity as stock,
    (p.price * wi.quantity) as inventory_value
FROM warehouse_inventory wi
JOIN products p ON wi.product_id = p.id
JOIN warehouses w ON wi.warehouse_id = w.id
JOIN cities c ON w.city_id = c.id
WHERE c.name = 'Lahore' AND wi.quantity > 0
ORDER BY inventory_value DESC;

-- =====================================================
-- 6. TEST SHORTEST PATH QUERY
-- =====================================================
SELECT '=== ROUTE CONNECTIVITY TEST ===' as test_section;

-- Check if major cities are connected
SELECT 
    'Lahore to Karachi' as route,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            WHERE c1.name = 'Lahore' AND c2.name = 'Karachi'
        ) THEN 'Direct Route Available'
        ELSE 'Requires Multi-hop'
    END as connectivity_status

UNION ALL

SELECT 
    'Islamabad to Peshawar' as route,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM routes r
            JOIN cities c1 ON r.from_city_id = c1.id
            JOIN cities c2 ON r.to_city_id = c2.id
            WHERE c1.name = 'Islamabad' AND c2.name = 'Peshawar'
        ) THEN 'Direct Route Available'
        ELSE 'Requires Multi-hop'
    END as connectivity_status;

-- =====================================================
-- 7. ORDER FULFILLMENT SIMULATION
-- =====================================================
SELECT '=== ORDER FULFILLMENT TEST ===' as test_section;

-- Find warehouses with Laptop in stock for customer in Faisalabad
SELECT 
    'Laptop order from Faisalabad' as scenario,
    w.name as warehouse,
    c.name as warehouse_city,
    wi.quantity as laptops_available,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM routes r
            JOIN cities customer_city ON customer_city.name = 'Faisalabad'
            JOIN cities warehouse_city ON warehouse_city.id = w.city_id
            WHERE (r.from_city_id = customer_city.id AND r.to_city_id = warehouse_city.id)
               OR (r.to_city_id = customer_city.id AND r.from_city_id = warehouse_city.id)
        ) THEN 'Deliverable'
        ELSE 'Route Check Needed'
    END as delivery_status
FROM warehouse_inventory wi
JOIN warehouses w ON wi.warehouse_id = w.id
JOIN cities c ON w.city_id = c.id
JOIN products p ON wi.product_id = p.id
WHERE p.name = 'Laptop' AND wi.quantity > 0;

-- =====================================================
-- 8. PERFORMANCE CHECK
-- =====================================================
SELECT '=== PERFORMANCE INDEXES ===' as test_section;

SHOW INDEX FROM products WHERE Key_name != 'PRIMARY';
SHOW INDEX FROM cities WHERE Key_name != 'PRIMARY';
SHOW INDEX FROM routes WHERE Key_name != 'PRIMARY';

-- =====================================================
-- 9. SAMPLE ORDERS
-- =====================================================
SELECT '=== EXISTING ORDERS ===' as test_section;

SELECT 
    o.id,
    o.product_name,
    o.customer_city,
    o.warehouse_city,
    o.delivery_distance,
    o.total_price,
    o.status,
    DATE(o.order_date) as order_date
FROM orders o
ORDER BY o.order_date DESC;

-- =====================================================
-- 10. FINAL STATUS
-- =====================================================
SELECT '=== SETUP VERIFICATION COMPLETE ===' as test_section;

SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM cities) >= 15 
         AND (SELECT COUNT(*) FROM products) >= 15
         AND (SELECT COUNT(*) FROM warehouses) >= 3
         AND (SELECT COUNT(*) FROM routes) >= 20
         AND (SELECT COUNT(*) FROM warehouse_inventory) >= 40
        THEN '✅ Database setup is COMPLETE and ready for testing!'
        ELSE '❌ Database setup is INCOMPLETE - check missing data'
    END as setup_status;

-- Show what to test next
SELECT 'Next steps:' as instruction, 
       '1. Update DBConnection.java with your MySQL credentials' as step_1,
       '2. Download mysql-connector-java-8.0.33.jar' as step_2,
       '3. Run compile_and_run.bat to start the application' as step_3,
       '4. Try: list products, order Laptop from Lahore' as step_4;
