package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager for MySQL
 * Handles connection pooling and database connectivity
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/store_algorithms";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
    private static Connection connection = null;

    /**
     * Get database connection (singleton pattern)
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection!");
            e.printStackTrace();
        }
    }

    /**
     * Test database connectivity
     * @return true if connection successful
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
