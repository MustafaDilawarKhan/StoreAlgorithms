package engine;

import commands.*;
import engine.CommandParser.ParsedCommand;

/**
 * Command Handler
 * Routes parsed commands to appropriate command implementations
 */
public class CommandHandler {
    private final ListProductsCommand listProductsCommand;
    private final OrderCommand orderCommand;
    private final RouteCommand routeCommand;
    private final HelpCommand helpCommand;

    public CommandHandler() {
        this.listProductsCommand = new ListProductsCommand();
        this.orderCommand = new OrderCommand();
        this.routeCommand = new RouteCommand();
        this.helpCommand = new HelpCommand();
    }

    /**
     * Handle user command
     * @param input Raw user input
     */
    public void handleCommand(String input) {
        ParsedCommand parsedCommand;
        
        // Use specialized parsers for complex commands
        if (input.toLowerCase().startsWith("order ") && input.toLowerCase().contains(" from ")) {
            parsedCommand = CommandParser.parseOrderCommand(input);
        } else if (input.toLowerCase().startsWith("show route ") && input.toLowerCase().contains(" to ")) {
            parsedCommand = CommandParser.parseRouteCommand(input);
        } else {
            parsedCommand = CommandParser.parse(input);
        }
        
        String command = parsedCommand.getCommand();
        
        switch (command) {
            case "list":
                if (parsedCommand.hasArguments() && parsedCommand.getArgument(0).equals("products")) {
                    listProductsCommand.execute(parsedCommand.getArguments());
                } else {
                    System.out.println("❌ Usage: list products");
                }
                break;
                
            case "order":
                orderCommand.execute(parsedCommand.getArguments());
                break;
                
            case "route":
                routeCommand.execute(parsedCommand.getArguments());
                break;

            case "help":
                helpCommand.execute(parsedCommand.getArguments());
                break;
                
            case "clear":
                clearScreen();
                break;
                
            default:
                System.out.println("❌ Unknown command: " + command);
                System.out.println("💡 Type 'help' for available commands");
                break;
        }
    }
    
    /**
     * Clear the console screen
     */
    private void clearScreen() {
        try {
            // For Windows
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Unix/Linux/Mac
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Fallback: print multiple newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
