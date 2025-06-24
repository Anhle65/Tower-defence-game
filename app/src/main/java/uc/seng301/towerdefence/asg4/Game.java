package uc.seng301.towerdefence.asg4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import uc.seng301.towerdefence.asg4.accessor.EnemyAccessor;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;
import uc.seng301.towerdefence.asg4.accessor.RoundAccessor;
import uc.seng301.towerdefence.asg4.accessor.TowerAccessor;
import uc.seng301.towerdefence.asg4.battle.BattleEnemy;
import uc.seng301.towerdefence.asg4.battle.BattleHandler;
import uc.seng301.towerdefence.asg4.battle.BattleRoundHandler;
import uc.seng301.towerdefence.asg4.cli.CommandLineInterface;
import uc.seng301.towerdefence.asg4.decorator.EnemyComponent;
import uc.seng301.towerdefence.asg4.enemies.EnemyGenerator;
import uc.seng301.towerdefence.asg4.enemies.EnemyProxy;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Round;
import uc.seng301.towerdefence.asg4.model.Tower;

/**
 * Main game loop functionality for application
 */
public class Game {
    private static final Logger LOGGER = LogManager.getLogger(Game.class);
    private static final int MAGIC_DISTANCE_NUMBER = 50;
    private static final int ROUND_STEP_DELAY = 200;

    private final CommandLineInterface cli;
    private final PlayerAccessor playerAccessor;
    private final RoundAccessor roundAccessor;
    private final TowerAccessor towerAccessor;
    private final EnemyAccessor enemyAccessor;
    private final EnemyGenerator enemyGenerator;
    private final boolean isDelayed;
    private double damageHitInWeather;


    private static final String WELCOME_MESSAGE = """
            ######################################################
                         Welcome to SENG301 Tower Defence App
            ######################################################""";

    private static final String HELP_MESSAGE =
            """
                    Available Commands:
                    "create_player <name>" to create a new player.
                    "create_round <player_name> <round_number> <round_distance>" create a round with <round_number> for player <player_name> of <round_distance>m for enemies to cover.
                    "create_tower <player_name> <tower_name> <damage> <type>" create a tower <tower_name> for player <player_name> with <damage> damage per attack, and any type from 'basictower', 'fastratetower', or 'longrangetower'.
                    "add_enemies <player_name> <round_number> <number_of_enemies> <auto_retry>" add a <number_of_enemies> of randomly generated enemies to the round <round_number>, set <auto_retry> to true to fetch new enemies if a duplicate was given (optional, true by default).
                    "play_round <player_name> <round_number>" play against the given round with the players' current towers.
                    "print <player_name>" print player by name.
                    "list_players print all player names.
                    "exit", "!q" to quit.
                    "help" print this help text.""";

    private static final String BAD_COMMAND = "Command incorrect use \"help\" for more information";

    /**
     * Create a new game with default settings
     */
    public Game() {
        // this will load the config file (hibernate.cfg.xml in resources folder)
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        roundAccessor = new RoundAccessor(sessionFactory);
        towerAccessor = new TowerAccessor(sessionFactory);
        enemyAccessor = new EnemyAccessor(sessionFactory);
        enemyGenerator = new EnemyProxy();
        cli = new CommandLineInterface(System.in, System.out);
        isDelayed = true;
    }

    /**
     * Create a new game with custom enemy generation, command line interface, and existing session
     * factory
     *
     * @param customEnemyGenerator Custom enemy generator implementation to get around calling the
     *        API
     * @param commandLineInterface Custom command line interface to get input from other sources
     * @param sessionFactory Existing session factory to use for accessing H2
     * @param isDelayed True iff the game should be printed with delays for readability
     */
    public Game(EnemyGenerator customEnemyGenerator, CommandLineInterface commandLineInterface,
            SessionFactory sessionFactory, boolean isDelayed) {
        playerAccessor = new PlayerAccessor(sessionFactory);
        roundAccessor = new RoundAccessor(sessionFactory);
        towerAccessor = new TowerAccessor(sessionFactory);
        enemyAccessor = new EnemyAccessor(sessionFactory);
        enemyGenerator = customEnemyGenerator;
        cli = commandLineInterface;
        this.isDelayed = isDelayed;
    }

    /**
     * Main application/game loop
     */
    public void play() {
        boolean more = true;
        cli.printLine(WELCOME_MESSAGE);
        cli.printLine(HELP_MESSAGE);
        while (more) {
            String input = cli.getNextLine();
            LOGGER.info("User input: {}", input);
            switch (input.split(" ")[0]) {
                case "create_player", "cpl" -> createPlayer(input);
                case "create_round", "cro" -> createRound(input);
                case "create_tower", "cto" -> createTower(input);
                case "add_enemies", "add" -> addEnemies(input);
                case "play_round", "pro" -> playRound(input);
                case "print" -> printPlayer(input);
                case "list_players" -> listPlayers();
                case "exit", "!q" -> {
                    more = false;
                    exit();
                }
                case "help" -> help();
                default -> {
                    cli.printLine(BAD_COMMAND);
                    LOGGER.info("User entered invalid input, {}", input);
                }
            }
        }
    }

    /**
     * Functionality for the create_player command
     *
     * @param input user input to the command
     */
    public void createPlayer(String input) {
        String[] uInputs = splitCommandArguments(input);
        Player player;
        if (uInputs.length != 2) {
            cli.printLine(BAD_COMMAND);
            return;
        }
        uInputs[1] = uInputs[1].replace("\"", "");
        try {
            player = playerAccessor.createPlayer(uInputs[1]);
        } catch (IllegalArgumentException e) {
            cli.printLine(
                    String.format("Could not create Player. %s: %s", e.getMessage(), uInputs[1]));
            LOGGER.warn(e);
            return;
        }
        playerAccessor.persistPlayer(player);
        LOGGER.info("Valid input, created user {}: {}", player.getPlayerId(), player.getName());
        cli.printLine(
                String.format("Created player %d: %s", player.getPlayerId(), player.getName()));
    }

    /**
     * Functionality for the create_round command
     *
     * @param input user input to the command
     */
    public void createRound(String input) {
        String[] uInputs = splitCommandArguments(input);
        Round round;
        if (uInputs.length != 4) {
            cli.printLine(BAD_COMMAND);
            LOGGER.info("Incorrect number of arguments passed to createRound");
            return;
        }
        uInputs[1] = uInputs[1].replace("\"", "");
        Player player = getPlayerByName(uInputs[1]);
        if (player == null) {
            return;
        }
        try {
            round = roundAccessor.createRound(Integer.parseInt(uInputs[2]),
                    Integer.parseInt(uInputs[3]), player, new ArrayList<>());
            roundAccessor.persistRound(round);

        } catch (NumberFormatException nfe) {
            cli.printLine(
                    String.format("Error parsing %s or %s to integer", uInputs[2], uInputs[3]));
            LOGGER.warn(nfe);
            return;
        } catch (IllegalArgumentException e) {
            cli.printLine(
                    String.format("Could not create round. %s: %s", e.getMessage(), uInputs[2]));
            LOGGER.warn(e);
            return;
        }
        LOGGER.info("Valid input, created round {} for user {} with name {}", round.getRoundId(),
                player.getPlayerId(), round.getNumber());
        cli.printLine(String.format("Created round %d: %s for %s", round.getRoundId(),
                round.getNumber(), player.getName()));
    }

    /**
     * Functionality for the create_tower command
     *
     * @param input user input to the command
     */
    public void createTower(String input) {
        String[] uInputs = splitCommandArguments(input);
        Tower tower;
        if (uInputs.length != 5) {
            cli.printLine(BAD_COMMAND);
            return;
        }
        uInputs[1] = uInputs[1].replace("\"", "");
        Player player = getPlayerByName(uInputs[1]);
        if (player == null) {
            return;
        }
        try {
            tower = towerAccessor.createTower(uInputs[2], player, Integer.parseInt(uInputs[3]),
                    uInputs[4]);
            player.getTowers().add(tower);
        } catch (NumberFormatException nfe) {
            cli.printLine(BAD_COMMAND);
            LOGGER.warn(nfe);
            return;
        } catch (IllegalArgumentException e) {
            cli.printLine(
                    String.format("Could not create tower. %s: %s", e.getMessage(), uInputs[2]));
            LOGGER.warn(e);
            return;
        }
        towerAccessor.persistTower(tower);
        LOGGER.info("Valid input, created tower {} for user {} with name {} and damage {}",
                tower.getTowerId(), player.getPlayerId(), tower.getName(), tower.getDamage());
        cli.printLine(String.format("Created tower %d: %s for %s", tower.getTowerId(),
                tower.getName(), player.getName()));
    }

    /**
     * Functionality for the add_enemies command
     *
     * @param input user input to the command
     */
    public void addEnemies(String input) {
        String[] uInputs = splitCommandArguments(input);
        if (uInputs.length == 4) {
            String[] newUInputs =
                    new String[] {uInputs[0], uInputs[1], uInputs[2], uInputs[3], "true"};
            uInputs = newUInputs;
        }
        if (uInputs.length != 5) {
            cli.printLine(BAD_COMMAND);
            return;
        }

        String playerName = uInputs[1].replace("\"", "");
        Player player = getPlayerByName(playerName);
        if (player == null) {
            return;
        }

        try {
            int roundNumber = Integer.parseInt(uInputs[2]);
            int numEnemies = Integer.parseInt(uInputs[3]);
            boolean autoRetry = Boolean.parseBoolean(uInputs[4]);
            Round round = roundAccessor.getPlayerRoundByNumber(player, roundNumber);
            if (null == round) {
                cli.printLine(
                        String.format("Round %s not found for player %s", roundNumber, playerName));
                return;
            }
            if (!validateEnemyCount(round, numEnemies)) {
                return;
            }

            addEnemiesToRound(round, numEnemies, autoRetry);
            roundAccessor.mergeRound(round);

        } catch (NumberFormatException nfe) {
            cli.printLine(
                    String.format("Error parsing %s or %s to integer", uInputs[2], uInputs[3]));
            LOGGER.warn(nfe);
        }
    }

    /**
     * Helper function to ask user to add enemies to a round
     *
     * @param round round to add enemies to
     * @param numEnemies number of enemies to add
     * @param autoRetry true if the user should not be asked to retry, and duplicates will retried
     */
    private void addEnemiesToRound(Round round, int numEnemies, boolean autoRetry) {
        int addedEnemies = 0;
        while (addedEnemies < numEnemies) {
            Enemy enemy = enemyGenerator.getRandomEnemy();
            if (enemyAccessor.getRoundEnemies(round).stream()
                    .noneMatch(e -> e.getName().equals(enemy.getName()))) {
                persistEnemyToRound(round, enemy);
                addedEnemies++;
            } else {
                cli.printLine(String.format(
                        "Enemy %s already exists in round %d, fetching another? Yes/No",
                        enemy.getName(), round.getNumber()));
                if (autoRetry) {
                    cli.printLine("auto-retry is enabled, fetching another enemy");
                } else {
                    String choice = getUserChoice();
                    if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
                        cli.printLine("Attempting to fetch another enemy");
                    } else if (choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("no")) {
                        cli.printLine("Enemy ignored");
                        addedEnemies++;
                    }
                }
            }
        }
    }

    /**
     * Helper function to persist an enemy to a round
     *
     * @param round round to add the enemy to
     * @param enemy enemy to add
     */
    private void persistEnemyToRound(Round round, Enemy enemy) {
        enemy.setRound(round);
        enemyAccessor.persistEnemy(enemy);
        round.addEnemy(enemy);
        cli.printLine(String.format("Fetched %s", enemy.toString()));
        LOGGER.info("Added enemy {} to round {}", enemy.getEnemyId(), round.getRoundId());
    }

    /**
     * Helper method to get a valid user choice (Yes/No)
     *
     * @return the user's choice as a string
     */
    private String getUserChoice() {
        String choice = cli.getNextLine();
        while (!choice.equalsIgnoreCase("y") && !choice.equalsIgnoreCase("n")
                && !choice.equalsIgnoreCase("yes") && !choice.equalsIgnoreCase("no")) {
            cli.printLine("Invalid option, please input Yes or No");
            choice = cli.getNextLine();
        }
        return choice;
    }

    /**
     * Functionality for the play_round command
     *
     * @param input user input to the command
     */
    public void playRound(String input) {
        String[] uInputs = splitCommandArguments(input);
        if (uInputs.length != 3) {
            cli.printLine(BAD_COMMAND);
            return;
        }
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (player == null) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        if (player.getTowers().isEmpty()) {
            cli.printLine(String.format(
                    "Player %s does not have any towers to fight the round with.", uInputs[1]));
            return;
        }
        if (!uInputs[2].matches("\\d+")) {
            cli.printLine(String.format("Round number %s is not a valid number", uInputs[2]));
            return;
        }
        Round round = roundAccessor.getPlayerRoundByNumber(player, Integer.parseInt(uInputs[2]));
        if (null == round) {
            cli.printLine(
                    String.format("Round %s not found for player %s", uInputs[2], uInputs[1]));
            return;
        }
        if (round.getEnemies().isEmpty()) {
            cli.printLine(
                    String.format("Round %s does not have any enemies to fight.", uInputs[2]));
            return;
        }

        cli.printLine(String.format("Playing round %s for player %s", uInputs[2], uInputs[1]));
        runBattle(new BattleRoundHandler(round), player.getTowers());
    }

    /**
     * Functionality running the battle for a round
     *
     * @param battleRound round to play
     * @param towers list of towers to use in the round
     */
    private void runBattle(BattleHandler battleRound, List<Tower> towers) {
        boolean gaming = true;
        int gameStep = 0;
        while (gaming) {
            moveEnemies(battleRound, gameStep, towers);
            if (!battleRound.anyEnemiesLeft()) {
                cli.printLine("Game won");
                gaming = false;
            } else {
                if (battleRound.anyEnemiesInRange()) {
                    cli.printLine("Game lost");
                    gaming = false;
                }
            }
            gameStep++;
            if (isDelayed) {
                try {
                    Thread.sleep(ROUND_STEP_DELAY);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread interrupted while sleeping", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Helper function to advance the enemies in the round and apply damage to them. Prints the
     * state of health for each enemy
     *
     * @param round current battle round
     * @param gameTick current game tick (to know the distance travelled)
     * @param towers list of towers inflicting damage to the enemies
     */
    private void moveEnemies(BattleHandler round, int gameTick, List<Tower> towers) {
        StringBuilder roundString = new StringBuilder();
        roundString.append("[Step ").append(gameTick + 1).append("]");
        round.resetEnemyTraversal();
        while (round.hasNextEnemy()) {
            EnemyComponent enemy = (EnemyComponent) round.getNextEnemy();
            enemy.updateDistance(enemy.getSpeed());

            float currentDistanceFraction =
                    enemy.getDistance() / (float) ((BattleRoundHandler) round).getDistance();
            String leftPadding =
                    " ".repeat((int) (MAGIC_DISTANCE_NUMBER * currentDistanceFraction));
            String rightPadding =
                    " ".repeat(Math.max(MAGIC_DISTANCE_NUMBER - leftPadding.length(), 0));
            roundString.append(String.format("%n%s[%s : %s]%s%s", leftPadding,
                    enemy.getHealth() > 0 ? enemy.getEmoji() : "\uD83E\uDEA6",
                    enemy.getDisplayableHealth(), rightPadding,
                    enemy.getDistance() > ((BattleRoundHandler) round).getDistance() ? "❌"
                            : "\uD83C\uDFC1"));
        }
        cli.printLine(roundString.toString());
        round.applyDamageToEnemies(towers);
    }

    /**
     * Functionality for the list command
     */
    public void listPlayers() {
        List<Player> players = playerAccessor.getAllPlayers();
        if (players.isEmpty()) {
            cli.printLine("No players found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Players:\n");
        for (Player player : players) {
            cli.printLine(player.toString());
        }
    }

    /**
     * Functionality for the print the content of a player
     *
     * @param input user input to the command
     */
    public void printPlayer(String input) {
        String[] uInputs = splitCommandArguments(input);
        if (uInputs.length != 2) {
            cli.printLine(BAD_COMMAND);
            return;
        }
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (player == null) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        cli.printLine(player.toString());
    }

    /**
     * Functionality for the exit command
     */
    public void exit() {
        LOGGER.info("User quitting application.");
    }

    /**
     * Functionality for the help command
     */
    public void help() {
        cli.printLine(HELP_MESSAGE);
    }

    /**
     * @return the playerAccessor
     */
    public PlayerAccessor getPlayerAccessor() {
        return playerAccessor;
    }

    /**
     * Helper function to get a player by name
     *
     * @param playerName name of the player to get
     * @return Player object if found, null otherwise
     */
    private Player getPlayerByName(String playerName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        if (player == null) {
            cli.printLine(String.format("No player named: %s", playerName));
        }
        return player;
    }

    /**
     * Helper function to validate the number of enemies to add
     *
     * @param round round to check the number of enemies for
     * @param numEnemies number of enemies to add
     * @return true if between 0 and 10, false otherwise
     */
    private boolean validateEnemyCount(Round round, int numEnemies) {
        if (numEnemies <= 0 || numEnemies > 10 || numEnemies + round.getEnemies().size() > 10) {
            cli.printLine(String.format(
                    "Invalid number of enemies %d for round %d, must be in the range [1-10]",
                    numEnemies + round.getEnemies().size(), round.getNumber()));
            return false;
        }
        return true;
    }

    /**
     * Split given string on space char, taking into consideration arguments enclosed between double
     * quotes
     *
     * Adapted from aioobe's SO post - https://stackoverflow.com/a/7804472 (CC BY-SA 3.0)
     *
     * @param commandArgs arguments to split on the space char, unless enclosed in double quotes
     * @return an array of each argument decomposed, stripping the quotes
     */
    private String[] splitCommandArguments(String commandArgs) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\"[^\"]++\")\\s*").matcher(commandArgs);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        return list.toArray(new String[0]);
    }
}
