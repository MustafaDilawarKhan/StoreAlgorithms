package engine;

import utils.Printer;
import java.util.Scanner;

/**
 * Core CLI Shell Engine
 * Manages the main input loop and user interaction
 */
public class ShellEngine {
    private final Scanner scanner;
    private final CommandHandler commandHandler;
    private boolean running;

    public ShellEngine() {
        this.scanner = new Scanner(System.in);
        this.commandHandler = new CommandHandler();
        this.running = true;
    }

    /**
     * Start the CLI shell
     */
    public void start() {
        Printer.printWelcome();
        
        while (running) {
            System.out.print("StoreAlgorithms> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                stop();
                break;
            }
            
            try {
                commandHandler.handleCommand(input);
            } catch (Exception e) {
                System.err.println("❌ Error executing command: " + e.getMessage());
                System.out.println("💡 Type 'help' for available commands");
            }
        }
    }

    /**
     * Stop the shell engine
     */
    public void stop() {
        running = false;
        scanner.close();
        Printer.printGoodbye();
    }

    /**
     * Check if shell is running
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }
}
