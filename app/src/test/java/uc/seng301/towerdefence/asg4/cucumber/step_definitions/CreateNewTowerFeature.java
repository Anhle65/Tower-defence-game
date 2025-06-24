package uc.seng301.towerdefence.asg4.cucumber.step_definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.towerdefence.asg4.Game;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;
import uc.seng301.towerdefence.asg4.accessor.TowerAccessor;
import uc.seng301.towerdefence.asg4.cli.CommandLineInterface;
import uc.seng301.towerdefence.asg4.enemies.EnemyGenerator;
import uc.seng301.towerdefence.asg4.enemies.EnemyService;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Tower;

public class CreateNewTowerFeature {

    private static TowerAccessor towerAccessor;
    private static PlayerAccessor playerAccessor;
    private static CommandLineInterface cli;
    private static Game game;
    private static List<String> output;

    @BeforeAll
    public static void before_or_after_all() {
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        towerAccessor = new TowerAccessor(sessionFactory);
        playerAccessor = new PlayerAccessor(sessionFactory);

        EnemyGenerator enemyGeneratorSpy = Mockito.spy(new EnemyService());
        cli = Mockito.mock(CommandLineInterface.class);
        game = new Game(enemyGeneratorSpy, cli, sessionFactory, false);

        // Capture standard out to test and for debugging purposes
        output = new ArrayList<>();
        Mockito.doAnswer((i) -> {
            output.add(i.getArgument(0));
            System.out.println((String) i.getArgument(0));
            return null;
        }).when(cli).printLine(Mockito.anyString());
    }

    // AC1-5 - i_create_a_player_named from CreateNewRoundFeature

    // AC1-5
    @When("I add a tower named {string} to player {string} with attack {int}")
    public void i_add_a_tower_named_to_player_with_attack(String towerName, String playerName,
            Integer attack) {
        game.createTower(String.format("create_tower \"%s\" \"%s\" %d %s", playerName, towerName,
                attack, "basictower"));
    }

    // AC1
    @Then("the tower {string} is added to {string} towers and has {int} attack")
    public void the_tower_is_added_to_towers(String towerName, String playerName, int attack) {
        Optional<Tower> tower =
                towerAccessor.getPlayerTowers(playerAccessor.getPlayerByName(playerName)).stream()
                        .filter(t -> t.getName().equals(towerName)).findFirst();
        Assertions.assertTrue(tower.isPresent());
        Assertions.assertEquals(tower.get().getName(), towerName);
        Assertions.assertEquals(tower.get().getDamage(), attack);
    }

    // AC2-5
    @Then("I am told I can not create the tower")
    public void i_am_told_I_can_not_create_the_tower() {

        Assertions.assertTrue(output.getLast().contains("Could not create tower"));
    }

    // AC5
    @Given("player {string} already has {int} towers")
    public void player_already_has_towers(String playerName, Integer numTowers) {
        Player player = playerAccessor.getPlayerByName(playerName);
        for (int i = 0; i < numTowers; i++) {
            Tower tower = towerAccessor.createTower("Tower" + i, player, 1, "basictower");
            player.getTowers().add(tower);
            towerAccessor.persistTower(tower);
        }
    }
}
