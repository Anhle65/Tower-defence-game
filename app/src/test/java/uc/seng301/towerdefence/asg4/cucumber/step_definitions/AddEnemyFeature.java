package uc.seng301.towerdefence.asg4.cucumber.step_definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.towerdefence.asg4.Game;
import uc.seng301.towerdefence.asg4.accessor.EnemyAccessor;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;
import uc.seng301.towerdefence.asg4.accessor.RoundAccessor;
import uc.seng301.towerdefence.asg4.cli.CommandLineInterface;
import uc.seng301.towerdefence.asg4.enemies.EnemyGenerator;
import uc.seng301.towerdefence.asg4.enemies.EnemyService;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Round;

public class AddEnemyFeature {
    private static SessionFactory sessionFactory;
    private static PlayerAccessor playerAccessor;
    private static RoundAccessor roundAccessor;
    private static EnemyAccessor enemyAccessor;
    private static EnemyGenerator enemyGeneratorSpy;
    private static CommandLineInterface cli;
    private static Game game;
    private static List<String> output;
    public static int enemyIncrement = 0;

    @BeforeAll
    public static void beforeAll() {
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        roundAccessor = new RoundAccessor(sessionFactory);
        enemyAccessor = new EnemyAccessor(sessionFactory);

        // set up mockito to mock overridden calls (spy) to API
        enemyGeneratorSpy = Mockito.spy(new EnemyService());

        // mock command line, so we can inject user interactions
        cli = Mockito.mock(CommandLineInterface.class);
        game = new Game(enemyGeneratorSpy, cli, sessionFactory, false);

        // Capture standard out to test and for debugging purposes
        output = new ArrayList<>();
        Mockito.doAnswer((i) -> {
            output.add(i.getArgument(0));
            System.out.println((String) i.getArgument(0));
            return null;
        }).when(cli).printLine(Mockito.anyString());

        Mockito.doAnswer((Answer<Enemy>) invocation -> enemyAccessor
                .createEnemy("Wyvern" + enemyIncrement++, 2, 1)).when(enemyGeneratorSpy)
                .getRandomEnemy();
    }

    @Given("player {string} has an empty round numbered {int}")
    public void player_has_an_empty_round_numbered(String playerName, Integer roundNumber) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Round round = roundAccessor.createRound(roundNumber, 100, player, new ArrayList<>());
        Long roundId = roundAccessor.persistRound(round);
        Assertions.assertNotNull(roundId);
        Assertions.assertTrue(round.getEnemies().isEmpty());
    }

    @When("I add {int} enemies to {string} round {int}")
    public void i_add_enemies_to_a_round(Integer numberOfEnemies, String playerName,
            int roundNumber) {
        game.addEnemies(String.format("add_enemies \"%s\" %d %d", playerName, roundNumber,
                numberOfEnemies));
    }

    @Then("the {int} enemies are added to {string} round {int}")
    public void the_enemies_are_added_to_round(Integer numberOfEnemies, String playerName,
            Integer roundNumber) {
        Round round = roundAccessor.getPlayerRounds(playerAccessor.getPlayerByName(playerName))
                .stream().filter(r -> r.getNumber() == roundNumber).findFirst().get();
        Assertions.assertEquals(numberOfEnemies, round.getEnemies().size());
        Assertions.assertTrue(round.getEnemies().get(0).getName().contains("Wyvern"));
    }

    @Then("I am told I must add between {int} and {int} enemies")
    public void i_am_told_i_must_add_between_and_enemies(Integer minNumEnemies,
            Integer maxNumEnemies) {
        Assertions.assertTrue(
                output.getLast().contains(String.format("[%d-%d]", minNumEnemies, maxNumEnemies)));
    }

    @Given("There are already {int} enemies in {string} round {int}")
    public void there_are_already_enemies_in_round(Integer numberOfEnemies, String playerName,
            Integer roundNumber) {
        Round round = roundAccessor.getPlayerRounds(playerAccessor.getPlayerByName(playerName))
                .stream().filter(r -> r.getNumber() == roundNumber).findFirst().get();
        List<Enemy> enemies = IntStream.range(0, numberOfEnemies)
                .mapToObj(i -> enemyGeneratorSpy.getRandomEnemy()).toList();
        enemies.forEach(e -> {
            e.setRound(round);
            enemyAccessor.persistEnemy(e);
        });
        round.getEnemies().addAll(enemies);
        roundAccessor.mergeRound(round);
    }

    @Then("I am told the total number of enemies in a round cannot exceed {int}")
    public void i_am_told_the_total_number_of_enemies_in_a_round_cannot_exceed(
            Integer totalNumberOfEnemies) {
        output.getLast().contains(String.format("cannot be more than %d", totalNumberOfEnemies));
    }

    @When("I add {int} duplicate enemies to {string} round {int}")
    public void i_add_duplicate_enemies_to_a_round(Integer numberOfEnemies, String playerName,
            int roundNumber) {
        Enemy enemy = enemyAccessor.createEnemy("Flame Dragon", 3, 10);
        Mockito.when(enemyGeneratorSpy.getRandomEnemy()).thenReturn(enemy);
        // we need to prepare the response for mockito to supply "N" after
        // the "Duplicate" message from CLI, so the scenario can run
        Mockito.when(cli.getNextLine()).thenReturn("N");
        game.addEnemies(String.format("add_enemies %s %d %d false", playerName, roundNumber,
                numberOfEnemies));
    }

    @Then("I am told I cannot add the same enemy twice")
    public void i_am_told_I_cannot_add_the_same_enemy_twice() {
        // must check the one but last message, as the last one is the "Enemy ignored" message
        Assertions.assertTrue(
                output.get(output.size() - 2).contains("Flame Dragon already exists in round"));
    }
}
