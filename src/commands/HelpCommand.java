package commands;

import utils.Printer;

/**
 * Help Command
 * Displays available commands and usage instructions
 */
public class HelpCommand {

    public void execute(String[] args) {
        Printer.printHeader("Available Commands");
        
        System.out.println("📋 " + Printer.CYAN + "list products" + Printer.RESET +
                          " - Display all available products with prices and quantities");

        System.out.println("🔍 " + Printer.CYAN + "list products search <term>" + Printer.RESET +
                          " - Deep search products using DFS algorithm");

        System.out.println("🛒 " + Printer.CYAN + "order <product> from <city>" + Printer.RESET +
                          " - Place an order for a product from a specific city (uses Dijkstra)");

        System.out.println("🚀 " + Printer.CYAN + "order <product> from <city> fast" + Printer.RESET +
                          " - Place a fast delivery order (uses BFS for minimum hops)");
        
        System.out.println("🗺️  " + Printer.CYAN + "show route <city1> to <city2>" + Printer.RESET +
                          " - Display shortest route between two cities");

        System.out.println("🧹 " + Printer.CYAN + "clear" + Printer.RESET +
                          " - Clear the console screen");
        
        System.out.println("❓ " + Printer.CYAN + "help" + Printer.RESET + 
                          " - Show this help message");
        
        System.out.println("🚪 " + Printer.CYAN + "exit" + Printer.RESET + " or " + 
                          Printer.CYAN + "quit" + Printer.RESET + " - Exit the application");
        
        Printer.printSeparator();
        
        System.out.println(Printer.YELLOW + "📝 Examples:" + Printer.RESET);
        System.out.println("  • " + Printer.GREEN + "list products" + Printer.RESET);
        System.out.println("  • " + Printer.GREEN + "list products search laptop" + Printer.RESET + " (DFS search)");
        System.out.println("  • " + Printer.GREEN + "order Laptop from Lahore" + Printer.RESET + " (Dijkstra - shortest distance)");
        System.out.println("  • " + Printer.GREEN + "order Mobile from Karachi fast" + Printer.RESET + " (BFS - fastest delivery)");
        System.out.println("  • " + Printer.GREEN + "show route Lahore to Islamabad" + Printer.RESET);
        
        Printer.printSeparator();
        
        System.out.println(Printer.BLUE + "🏪 Features:" + Printer.RESET);
        System.out.println("  • Real-time product inventory from MySQL database");
        System.out.println("  • Smart delivery options: Fast (BFS) vs Cost-effective (Dijkstra)");
        System.out.println("  • Deep product search using DFS algorithm");
        System.out.println("  • Multi-warehouse order fulfillment optimization");
        System.out.println("  • Distance-based delivery cost calculation");
        
        System.out.println();
    }
}
