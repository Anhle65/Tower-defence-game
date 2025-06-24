package uc.seng301.towerdefence.asg4.cucumber.step_definitions;

import java.util.ArrayList;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.towerdefence.asg4.accessor.EnemyAccessor;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;
import uc.seng301.towerdefence.asg4.accessor.RoundAccessor;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Round;

public class CreateNewRoundFeature {
    private static RoundAccessor roundAccessor;
    private static PlayerAccessor playerAccessor;
    private static EnemyAccessor enemyAccessor;
    private Exception expectedException;

    @BeforeAll
    public static void before_or_after_all() {
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        roundAccessor = new RoundAccessor(sessionFactory);
        playerAccessor = new PlayerAccessor(sessionFactory);
        enemyAccessor = new EnemyAccessor(sessionFactory);
    }

    // AC 1.1, 2, 3
    @Given("I create a player named {string}")
    public void i_create_a_player_named(String playerName) {
        Player player = playerAccessor.createPlayer(playerName);
        Long id = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(playerAccessor.getPlayerById(id));
    }

    // AC1.1
    @Given("Player {string} has no rounds with the number {int}")
    public void player_has_no_rounds_with_the_number(String playerName, Integer roundNumber) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Assertions.assertTrue(
                player.getRounds().stream().noneMatch(round -> round.getNumber() == roundNumber));
    }

    // AC1.1, 2, 3
    @When("I create the round with number {int} and distance {int} for {string}")
    public void i_create_the_round_with_number_and_distance_for(Integer roundNumber,
            Integer roundDistance, String playerName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Round round =
                roundAccessor.createRound(roundNumber, roundDistance, player, new ArrayList<>());
        Long id = roundAccessor.persistRound(round);
        Assertions.assertNotNull(roundAccessor.getRoundById(id));
    }

    // AC1.1
    @Then("The round is created with number {int} for {string}")
    public void the_round_is_created_with_number_for(Integer roundNumber, String playerName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Assertions.assertTrue(
                player.getRounds().stream().anyMatch(round -> round.getNumber() == roundNumber));
    }

    // AC1.2
    @When("I create an invalid round with number {int} and distance {int} for {string}")
    public void i_create_an_invalid_round_with_number_and_distance_for(Integer roundNumber,
            Integer roundDistance, String playerName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        expectedException =
                Assertions.assertThrows(IllegalArgumentException.class, () -> roundAccessor
                        .createRound(roundNumber, roundDistance, player, new ArrayList<>()));
    }

    // AC2
    @When("I create a duplicate round with number {int} and distance {int} for {string}")
    public void i_create_a_duplicate_round_with_number_and_distance_for(Integer roundNumber,
            Integer roundDistance, String playerName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        expectedException =
                Assertions.assertThrows(IllegalArgumentException.class, () -> roundAccessor
                        .createRound(roundNumber, roundDistance, player, new ArrayList<>()));
    }

    // AC3
    @When("I add an enemy named {string} with speed {int} and health {int} in round {int} for {string}")
    public void i_add_an_enemy_named_with_speed_and_health_in_pack_for(String petName,
            Integer speed, Integer health, Integer roundNumber, String playerName) {
        Enemy enemy = enemyAccessor.createEnemy(petName, speed, health);
        Long id = enemyAccessor.persistEnemy(enemy);
        Assertions.assertNotNull(enemy = enemyAccessor.getEnemyById(id));
        Player player = playerAccessor.getPlayerByName(playerName);
        Assertions.assertNotNull(player);
        Round round = player.getRounds().stream().filter(r -> r.getNumber() == roundNumber)
                .findFirst().get();
        Assertions.assertNotNull(round);
        round.getEnemies().add(enemy);
        roundAccessor.mergeRound(round);
    }

    // AC3
    @Then("The round {int} of {string} includes an enemy named {string}")
    public void the_round_of_includes_an_enemy_named(Integer roundNumber, String playerName,
            String enemyName) {
        Player player = playerAccessor.getPlayerByName(playerName);
        Round round = player.getRounds().stream().filter(r -> roundNumber == r.getNumber())
                .findFirst().get();
        Assertions.assertTrue(
                round.getEnemies().stream().anyMatch(pet -> enemyName.equals(pet.getName())));
    }

    @Then("I cannot create that round")
    public void i_cannot_create_that_round() {
        Assertions.assertNotNull(expectedException);
    }
}
