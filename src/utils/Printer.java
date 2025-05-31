package utils;

/**
 * Utility class for formatted console output
 */
public class Printer {
    
    // ANSI Color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    /**
     * Print welcome message
     */
    public static void printWelcome() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║" + WHITE + "                    🏪 StoreAlgorithms                        " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + WHITE + "           E-Commerce Order Fulfillment Simulator             " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + WHITE + "                                                              " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + YELLOW + "  📦 Products  🏭 Warehouses  🚚 Shortest Path Delivery      " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + GREEN + "                    Type 'help' to get started                " + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }
    
    /**
     * Print goodbye message
     */
    public static void printGoodbye() {
        System.out.println();
        System.out.println(PURPLE + "👋 Thank you for using StoreAlgorithms!" + RESET);
        System.out.println(GREEN + "🚀 Happy coding and efficient deliveries!" + RESET);
    }
    
    /**
     * Print success message
     * @param message Success message
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✅ " + message + RESET);
    }
    
    /**
     * Print error message
     * @param message Error message
     */
    public static void printError(String message) {
        System.out.println(RED + "❌ " + message + RESET);
    }
    
    /**
     * Print warning message
     * @param message Warning message
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠️ " + message + RESET);
    }
    
    /**
     * Print info message
     * @param message Info message
     */
    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ️ " + message + RESET);
    }
    
    /**
     * Print product information
     * @param id Product ID
     * @param name Product name
     * @param price Product price
     * @param quantity Available quantity
     */
    public static void printProduct(int id, String name, double price, int quantity) {
        System.out.printf(CYAN + "📦 %d. %s " + YELLOW + "(Rs. %.0f) " + GREEN + "- Qty: %d%n" + RESET, 
                         id, name, price, quantity);
    }
    
    /**
     * Print route information
     * @param route Route string
     * @param totalDistance Total distance
     */
    public static void printRoute(String route, int totalDistance) {
        System.out.println(BLUE + "📍 " + route + YELLOW + " (Total: " + totalDistance + " km)" + RESET);
    }
    
    /**
     * Print section header
     * @param title Section title
     */
    public static void printHeader(String title) {
        System.out.println();
        System.out.println(PURPLE + "═══ " + title + " ═══" + RESET);
    }
    
    /**
     * Print separator line
     */
    public static void printSeparator() {
        System.out.println(CYAN + "────────────────────────────────────────────────────────────" + RESET);
    }
}
