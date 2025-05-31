package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command Parser
 * Parses user input into command and arguments
 */
public class CommandParser {
    
    /**
     * Parse command string into command and arguments
     * @param input Raw user input
     * @return ParsedCommand object
     */
    public static ParsedCommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ParsedCommand("", new String[0]);
        }
        
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        
        return new ParsedCommand(command, args);
    }
    
    /**
     * Parse complex commands like "order Laptop from Lahore"
     * @param input Raw user input
     * @return ParsedCommand with structured arguments
     */
    public static ParsedCommand parseOrderCommand(String input) {
        // Handle "order <product> from <city>" pattern
        if (input.toLowerCase().startsWith("order ") && input.toLowerCase().contains(" from ")) {
            String[] parts = input.split("(?i)\\s+from\\s+");
            if (parts.length == 2) {
                String productPart = parts[0].substring(6).trim(); // Remove "order "
                String cityPart = parts[1].trim();
                
                return new ParsedCommand("order", new String[]{productPart, cityPart});
            }
        }
        
        return parse(input);
    }
    
    /**
     * Parse route commands like "show route Lahore to Karachi"
     * @param input Raw user input
     * @return ParsedCommand with from and to cities
     */
    public static ParsedCommand parseRouteCommand(String input) {
        // Handle "show route <city1> to <city2>" pattern
        if (input.toLowerCase().startsWith("show route ") && input.toLowerCase().contains(" to ")) {
            String routePart = input.substring(11); // Remove "show route "
            String[] cities = routePart.split("(?i)\\s+to\\s+");
            if (cities.length == 2) {
                return new ParsedCommand("route", new String[]{cities[0].trim(), cities[1].trim()});
            }
        }

        return parse(input);
    }


    
    /**
     * Parsed Command Data Structure
     */
    public static class ParsedCommand {
        private final String command;
        private final String[] arguments;
        
        public ParsedCommand(String command, String[] arguments) {
            this.command = command;
            this.arguments = arguments;
        }
        
        public String getCommand() {
            return command;
        }
        
        public String[] getArguments() {
            return arguments;
        }
        
        public String getArgument(int index) {
            return index < arguments.length ? arguments[index] : "";
        }
        
        public int getArgumentCount() {
            return arguments.length;
        }
        
        public boolean hasArguments() {
            return arguments.length > 0;
        }
        
        @Override
        public String toString() {
            return "Command: " + command + ", Args: " + Arrays.toString(arguments);
        }
    }
}
