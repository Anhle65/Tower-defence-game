package uc.seng301.towerdefence.asg4.cucumber.step_definitions;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.towerdefence.asg4.Game;
import uc.seng301.towerdefence.asg4.accessor.EnemyAccessor;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;
import uc.seng301.towerdefence.asg4.accessor.RoundAccessor;
import uc.seng301.towerdefence.asg4.accessor.TowerAccessor;
import uc.seng301.towerdefence.asg4.cli.CommandLineInterface;
import uc.seng301.towerdefence.asg4.enemies.EnemyGenerator;
import uc.seng301.towerdefence.asg4.enemies.EnemyService;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Round;
import uc.seng301.towerdefence.asg4.model.Tower;

public class PlayRoundFeature {

    private static PlayerAccessor playerAccessor;
    private static RoundAccessor roundAccessor;
    private static EnemyAccessor enemyAccessor;
    private static TowerAccessor towerAccessor;
    private static Game game;
    private static List<String> output;

    @BeforeAll
    public static void beforeAll() {
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        roundAccessor = new RoundAccessor(sessionFactory);
        enemyAccessor = new EnemyAccessor(sessionFactory);
        towerAccessor = new TowerAccessor(sessionFactory);

        EnemyGenerator enemyGeneratorSpy = Mockito.spy(new EnemyService());
        CommandLineInterface cli = Mockito.mock(CommandLineInterface.class);
        game = new Game(enemyGeneratorSpy, cli, sessionFactory, false);

        output = new ArrayList<>();
        Mockito.doAnswer((i) -> {
            output.add(i.getArgument(0));
            System.out.println((String) i.getArgument(0));
            return null;
        }).when(cli).printLine(Mockito.anyString());
    }

    // AC1-5 - i_create_a_player_named from CreateNewRoundFeature

    // AC1, 2, 4, 5
    @Given("{string} has towers with damage")
    public void has_towers_with_damage(String playerName, DataTable towers) {
        Player player = playerAccessor.getPlayerByName(playerName);
        List<List<String>> towersData = towers.asLists(String.class);
        for (List<String> towerData : towersData) {
            Tower tower = towerAccessor.createTower(towerData.get(0), player,
                    Integer.parseInt(towerData.get(1)), "basictower");
            player.getTowers().add(tower);
            towerAccessor.persistTower(tower);
        }
        Assertions.assertEquals(playerAccessor.getPlayerByName(playerName).getTowers().size(),
                towersData.size());
    }

    // AC1
    @Given("{string} has no round")
    public void has_no_round(String playerName) {
        Assertions.assertTrue(playerAccessor.getPlayerByName(playerName).getRounds().isEmpty());
    }

    // AC1-5
    @When("{string} tries to play against round {int}")
    public void tries_to_play_against_round(String playerName, Integer roundNumber) {
        game.playRound(String.format("play_round \"%s\" %d", playerName, roundNumber));
    }

    // AC1
    @Then("I am told I can not play the round because the round {int} does not exist")
    public void i_am_told_i_can_not_play_the_round_does_not_exist(Integer roundNumber) {
        Assertions.assertTrue(
                output.getLast().contains(String.format("Round %s not found", roundNumber)));
    }

    // AC2 - has_an_empty_round_numbered from AddNewEnemyFeature

    // AC2
    @Then("I am told I can not play the round because the round {int} does not have any enemies")
    public void i_am_told_i_can_not_play_the_round(Integer roundNumber) {
        System.out.println(output.toString());
        Assertions.assertTrue(output.getLast()
                .contains(String.format("Round %d does not have any enemies", roundNumber)));
    }

    // AC3-5
    @Given("{string} has a round {int} with enemies")
    public void has_a_round_with_enemies(String playerName, Integer roundNumber,
            DataTable enemies) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Round round = roundAccessor.createRound(roundNumber, 100, player, new ArrayList<>());
        List<List<String>> strongEnemies = enemies.asLists(String.class);
        for (List<String> enemy : strongEnemies) {
            Enemy strongEnemy = enemyAccessor.createEnemy(enemy.get(0),
                    Integer.parseInt(enemy.get(1)), Integer.parseInt(enemy.get(2)));
            round.getEnemies().add(strongEnemy);
            enemyAccessor.persistEnemy(strongEnemy);
        }
        Assertions.assertEquals(round.getEnemies().size(), strongEnemies.size());
        roundAccessor.persistRound(round);
    }

    // AC3
    @Given("{string} has no towers")
    public void has_no_towers(String playerName) {
        Assertions.assertTrue(playerAccessor.getPlayerByName(playerName).getTowers().isEmpty());
    }

    // AC 3
    @Then("I am told I can not play the round because {string} does not have any towers")
    public void i_am_told_i_can_not_play_the_round(String playerName) {
        Assertions.assertTrue(output.getLast()
                .contains(String.format("Player %s does not have any towers", playerName)));
    }

    // AC4
    @Then("the player wins the round")
    public void the_player_wins_the_round() {
        Assertions.assertTrue(output.getLast().contains("Game won"));
    }

    // AC5
    @Then("the player loses the round")
    public void the_player_loses_the_round() {
        Assertions.assertTrue(output.getLast().contains("Game lost"));
    }

}
