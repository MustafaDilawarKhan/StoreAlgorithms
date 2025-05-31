package commands;

import dao.ProductDAO;
import models.Product;
import utils.Printer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * List Products Command
 * Displays all available products from the database
 */
public class ListProductsCommand {
    private final ProductDAO productDAO;

    public ListProductsCommand() {
        this.productDAO = new ProductDAO();
    }

    public void execute(String[] args) {
        try {
            // Check if this is a search command
            if (args.length > 0 && args[0].equalsIgnoreCase("search") && args.length > 1) {
                String searchTerm = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                performDFSSearch(searchTerm);
                return;
            }

            Printer.printHeader("Available Products");

            List<Product> products = productDAO.getAllProducts();

            if (products.isEmpty()) {
                Printer.printWarning("No products found in inventory!");
                System.out.println("💡 Please check database connection or add products to inventory.");
                return;
            }

            System.out.println(Printer.BLUE + "📦 Total Products: " + products.size() + Printer.RESET);
            Printer.printSeparator();

            for (Product product : products) {
                if (product.isInStock()) {
                    Printer.printProduct(product.getId(), product.getName(),
                                       product.getPrice(), product.getQuantity());
                } else {
                    System.out.printf(Printer.RED + "📦 %d. %s " + Printer.YELLOW + "(Rs. %.0f) " +
                                    Printer.RED + "- Out of Stock%n" + Printer.RESET,
                                    product.getId(), product.getName(), product.getPrice());
                }
            }

            Printer.printSeparator();

            // Show summary statistics
            long inStockCount = products.stream().filter(Product::isInStock).count();
            long outOfStockCount = products.size() - inStockCount;

            System.out.println(Printer.GREEN + "✅ In Stock: " + inStockCount + Printer.RESET +
                             " | " + Printer.RED + "❌ Out of Stock: " + outOfStockCount + Printer.RESET);

            System.out.println();
            System.out.println("💡 To place an order, use: " + Printer.CYAN +
                             "order <product_name> from <city>" + Printer.RESET);
            System.out.println("🔍 To search products, use: " + Printer.CYAN +
                             "list products search <term>" + Printer.RESET);

        } catch (Exception e) {
            Printer.printError("Failed to retrieve products: " + e.getMessage());
            System.out.println("🔧 Please check database connection and try again.");
        }
    }

    /**
     * Perform DFS-based deep search for products
     * @param searchTerm Search term for products
     */
    private void performDFSSearch(String searchTerm) {
        try {
            Printer.printHeader("🌲 DFS Deep Product Search: \"" + searchTerm + "\"");
            System.out.println("🔍 " + Printer.BLUE + "Algorithm used: DFS (explores product categories recursively)" + Printer.RESET);

            List<Product> foundProducts = productDAO.deepSearchProducts(searchTerm);

            if (foundProducts.isEmpty()) {
                Printer.printWarning("No products found matching: " + searchTerm);
                System.out.println("💡 Try searching for: laptop, mobile, electronics, etc.");
                return;
            }

            System.out.println(Printer.GREEN + "✅ Found " + foundProducts.size() + " products through DFS search" + Printer.RESET);
            Printer.printSeparator();

            // Group products by category for better display
            Map<String, List<Product>> productsByCategory = foundProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory() != null ? p.getCategory() : "Uncategorized"));

            for (Map.Entry<String, List<Product>> entry : productsByCategory.entrySet()) {
                String category = entry.getKey();
                List<Product> categoryProducts = entry.getValue();

                System.out.println(Printer.PURPLE + "📂 Category: " + category +
                                 " (" + categoryProducts.size() + " products)" + Printer.RESET);

                for (Product product : categoryProducts) {
                    if (product.isInStock()) {
                        Printer.printProduct(product.getId(), product.getName(),
                                           product.getPrice(), product.getQuantity());
                    } else {
                        System.out.printf(Printer.RED + "📦 %d. %s " + Printer.YELLOW + "(Rs. %.0f) " +
                                        Printer.RED + "- Out of Stock%n" + Printer.RESET,
                                        product.getId(), product.getName(), product.getPrice());
                    }
                }
                System.out.println();
            }

            Printer.printSeparator();
            System.out.println(Printer.BLUE + "💡 Why DFS is used here:" + Printer.RESET);
            System.out.println("  • DFS explores product categories recursively");
            System.out.println("  • Finds related products in the same category");
            System.out.println("  • Deep search through product hierarchy");
            System.out.println("  • Perfect for discovering similar items");

        } catch (Exception e) {
            Printer.printError("Product search failed: " + e.getMessage());
            System.out.println("🔧 Please check your search term and try again.");
        }
    }
}
