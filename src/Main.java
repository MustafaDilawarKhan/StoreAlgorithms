import engine.ShellEngine;
import config.DBConnection;
import utils.Printer;

/**
 * Main Application Entry Point
 * StoreAlgorithms - E-Commerce Order Fulfillment Simulator
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Test database connection
            if (DBConnection.testConnection()) {
                // Start the CLI shell
                ShellEngine shell = new ShellEngine();
                shell.start();
            } else {
                Printer.printError("Database connection failed!");
                System.out.println("🔧 Please ensure MySQL is running and database is configured.");
                System.out.println("📝 Check connection settings in config/DBConnection.java");
            }
        } catch (Exception e) {
            Printer.printError("Application startup failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up database connection
            DBConnection.closeConnection();
        }
    }
}