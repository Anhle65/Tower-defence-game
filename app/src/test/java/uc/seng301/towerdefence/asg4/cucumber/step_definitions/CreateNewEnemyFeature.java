package uc.seng301.towerdefence.asg4.cucumber.step_definitions;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.towerdefence.asg4.accessor.EnemyAccessor;
import uc.seng301.towerdefence.asg4.model.Enemy;

public class CreateNewEnemyFeature {
    private static EnemyAccessor enemyAccessor;
    private Exception expectedException;

    @BeforeAll
    public static void before_or_after_all() {
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        enemyAccessor = new EnemyAccessor(sessionFactory);
    }

    @Given("There is no enemy with name {string}")
    public void there_is_no_enemy_with_name(String enemyName) {
        Assertions.assertNull(enemyAccessor.getEnemyByName(enemyName));
    }

    @When("I create an enemy named {string} with speed: {int} and health: {int}")
    public void i_create_an_enemy_named_with_speed_and_health(String enemyName, Integer speed,
            Integer health) {
        Enemy enemy = enemyAccessor.createEnemy(enemyName, speed, health);
        Long id = enemyAccessor.persistEnemy(enemy);
        enemy = enemyAccessor.getEnemyById(id);
        Assertions.assertNotNull(enemy);
    }

    @Then("The enemy is created with the name {string}, speed: {int} and health: {int}")
    public void the_enemy_is_created_with_the_name_speed_and_health(String enemyName, Integer speed,
            Integer health) {
        Enemy enemy = enemyAccessor.getEnemyByName(enemyName);
        Assertions.assertNotNull(enemy);
        Assertions.assertEquals(enemyName, enemy.getName());
        Assertions.assertEquals(speed, enemy.getSpeed());
        Assertions.assertEquals(health, enemy.getHealth());
    }

    @When("I create an invalid enemy named {string} with speed: {int} and health: {int}")
    public void i_create_an_invalid_enemy_named_with_speed_and_health(String enemyName,
            Integer attack, Integer health) {
        expectedException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> enemyAccessor.createEnemy(enemyName, attack, health));
    }

    @Then("I cannot create the enemy")
    public void i_cannot_create_the_enemy() {
        Assertions.assertNotNull(expectedException);
    }
}
