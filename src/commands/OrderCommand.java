package commands;

import dao.OrderProcessor;
import models.Order;
import utils.Printer;

/**
 * Order Command
 * Handles product ordering with shortest path warehouse selection
 */
public class OrderCommand {
    private final OrderProcessor orderProcessor;

    public OrderCommand() {
        this.orderProcessor = new OrderProcessor();
    }

    public void execute(String[] args) {
        if (args.length < 2) {
            Printer.printError("Invalid order format!");
            System.out.println("💡 Usage: " + Printer.CYAN + "order <product_name> from <city>" + Printer.RESET);
            System.out.println("💡 Fast delivery: " + Printer.CYAN + "order <product_name> from <city> fast" + Printer.RESET);
            System.out.println("📝 Example: " + Printer.GREEN + "order Laptop from Lahore" + Printer.RESET);
            System.out.println("📝 Fast example: " + Printer.GREEN + "order Laptop from Lahore fast" + Printer.RESET);
            return;
        }

        String productName = args[0];
        String customerCity = args[1];
        boolean fastDelivery = args.length > 2 && args[2].equalsIgnoreCase("fast");

        try {
            if (fastDelivery) {
                Printer.printInfo("Processing FAST delivery order for " + productName + " from " + customerCity + "...");
                System.out.println("🚀 Using BFS algorithm for minimum hops (fastest delivery)");
            } else {
                Printer.printInfo("Processing order for " + productName + " from " + customerCity + "...");
                System.out.println("💰 Using Dijkstra algorithm for shortest distance (lowest cost)");
            }

            // Process the order using OrderProcessor with algorithm choice
            Order order = orderProcessor.processOrder(productName, customerCity, 1, fastDelivery);

            if (order != null && order.getStatus() == Order.OrderStatus.CONFIRMED) {
                displayOrderSuccess(order, fastDelivery);
            } else {
                handleOrderFailure(productName, customerCity);
            }

        } catch (Exception e) {
            Printer.printError("Order processing failed: " + e.getMessage());
            System.out.println("🔧 Please check your input and try again.");
        }
    }

    private void displayOrderSuccess(Order order, boolean fastDelivery) {
        Printer.printSeparator();
        if (fastDelivery) {
            Printer.printSuccess("FAST delivery order placed successfully!");
            System.out.println("🚀 " + Printer.BLUE + "Algorithm used: BFS (minimum hops for fastest delivery)" + Printer.RESET);
        } else {
            Printer.printSuccess("Order placed successfully!");
            System.out.println("💰 " + Printer.GREEN + "Algorithm used: Dijkstra (shortest distance for lowest cost)" + Printer.RESET);
        }
        
        System.out.println();
        System.out.println(Printer.BLUE + "📋 Order Details:" + Printer.RESET);
        System.out.println("  🏷️  Order ID: " + order.getId());
        System.out.println("  📦 Product: " + order.getProductName());
        System.out.println("  📊 Quantity: " + order.getQuantity());
        System.out.println("  💰 Product Price: Rs. " + String.format("%.2f", order.getTotalPrice()));
        System.out.println("  📍 Customer City: " + order.getCustomerCity());
        
        System.out.println();
        System.out.println(Printer.GREEN + "🏭 Fulfillment Details:" + Printer.RESET);
        System.out.println("  🏪 Warehouse: " + order.getWarehouseName());
        System.out.println("  🌆 Warehouse City: " + order.getWarehouseCity());
        System.out.println("  📏 Delivery Distance: " + order.getDeliveryDistance() + " km");
        System.out.println("  🚚 Delivery Cost: Rs. " + String.format("%.2f", order.getDeliveryCost()));
        
        System.out.println();
        System.out.println(Printer.YELLOW + "💳 Total Amount: Rs. " + 
                         String.format("%.2f", order.getFinalTotal()) + Printer.RESET);
        
        Printer.printSeparator();
        System.out.println("📦 Your order will be shipped from the nearest warehouse!");
        System.out.println("🚚 Estimated delivery: 2-3 business days");
    }

    private void handleOrderFailure(String productName, String customerCity) {
        Printer.printError("Unable to fulfill order!");
        
        System.out.println();
        System.out.println("❌ Possible reasons:");
        System.out.println("  • Product '" + productName + "' not found in inventory");
        System.out.println("  • Product is out of stock in all warehouses");
        System.out.println("  • City '" + customerCity + "' is not in our delivery network");
        System.out.println("  • No route available to the specified city");
        
        System.out.println();
        System.out.println("💡 Suggestions:");
        System.out.println("  • Check available products: " + Printer.CYAN + "list products" + Printer.RESET);
        System.out.println("  • Verify city name spelling");
        System.out.println("  • Try ordering from a different city");
    }
}
