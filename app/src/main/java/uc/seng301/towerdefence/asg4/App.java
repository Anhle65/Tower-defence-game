package uc.seng301.towerdefence.asg4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class of the application. It initializes the game and starts the main game loop.
 */
public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static Game game;


    public App() {
        // Initialize the game instance
        game = new Game();
    }

    /**
     * Application entry point, runs the main game loop until player quits
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        new App();
        LOGGER.info("Starting application...");
        game.play();
        LOGGER.info("Exiting application...");
    }

    /**
     * Get the game instance
     *
     * @return the game instance
     */
    public Game getGame() {
        return game;
    }

}
