package uc.seng301.towerdefence.asg4.battle;

import java.util.*;

import uc.seng301.towerdefence.asg4.cli.CommandLineInterface;
import uc.seng301.towerdefence.asg4.context.WeatherContext;
import uc.seng301.towerdefence.asg4.decorator.*;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Round;
import uc.seng301.towerdefence.asg4.model.Tower;

/**
 * BattleRoundHandler is a class that implements the BattleRound interface and handles the battle
 * round logic in the game.
 * It manages the enemies in the round, applies damage to them based on the towers, and provides
 * methods to traverse and check the status of the enemies.
 */
public class BattleRoundHandler extends Round implements BattleHandler {
    private final CommandLineInterface cli;
    private List<Enemy> enemies = new ArrayList<>();
    private Iterator<Enemy> iterator;
    private final WeatherContext weatherContext;
    private final BoostingDamageRandomization randomMultiplication;
    private final ActivateSpellRandomization activateSpellRandomization;
    private double multiplication = 0;
    boolean castSpell = false;
    boolean canCastSpell = true;
    private int spellLayers = 0;
    /**
     * Default constructor for BattleRoundHandler
     * 
     * @param round The round to copy
     * @throws IllegalArgumentException If the round is null
     */
    public BattleRoundHandler(Round round) {
        if (null == round) {
            throw new IllegalArgumentException("Round cannot be null");
        }
        cli = new CommandLineInterface(System.in, System.out);
        weatherContext = new WeatherContext();
        setRoundId(round.getRoundId());
        setNumber(round.getNumber());
        setDistance(round.getDistance());
        setPlayer(round.getPlayer());
        round.getEnemies().forEach(enemy -> this.enemies.add(new BattleEnemy(enemy)));
        iterator = this.enemies.iterator();
        randomMultiplication = new BoostingDamageRandomization();
        activateSpellRandomization = new ActivateSpellRandomization();
    }

    @Override
    public Iterator<Enemy> getEnemyTraversal() {
        return iterator;
    }

    @Override
    public Enemy getNextEnemy() {
        if (!hasNextEnemy()) {
            throw new IllegalStateException("No more enemies to traverse");
        }
        return iterator.next();
    }

    @Override
    public boolean hasNextEnemy() {
        return iterator.hasNext();
    }

    @Override
    public void resetEnemyTraversal() {
        iterator = this.enemies.iterator();
    }

    /**
     * Randomly decides whether to cast a spell on the enemies in this round.
     * If the tower is on cooldown, it will not cast a spell regardless of the magic number.
     * If the magic number is 0, it casts a spell and increases the number of spells.
     * Otherwise, it does not cast a spell.
     */
    private void mayCastingSpell() {
        int magicNumber = activateSpellRandomization.generateRandomActivation();
        if(!canCastSpell) {
            cli.printLine("Tower need to wait for cooldown time to spell again.");
        } else {
            if (magicNumber == 0) {
                castSpell = true;
                activateSpellRandomization.increaseNumberOfSpells();
                multiplication = randomMultiplication.generateRandomMultiplication();
                cli.printLine("New spell applies with extra: " + multiplication + "% damage boost in next 3 steps.");
            } else {
                castSpell = false;
            }
        }
    }

    /**
     * Applies damage to the enemies in this round based on the towers.
     * It updates the weather state and calculates the weather affected damage.
     * Then applies the damage accordingly spell layers.
     * @param towers The towers to apply damage from
     */
    public void applyDamageToEnemies(List<Tower> towers) {
        resetEnemyTraversal();
        List<Enemy> updatedEnemies = new ArrayList<>();
        if (spellLayers >= towers.size() && towers.size() < 3) {
            castSpell = false;
            canCastSpell = false;
        } else {
            canCastSpell = true;
        }
        mayCastingSpell();
        while (hasNextEnemy()) {
            EnemyComponent enemy = (EnemyComponent) getNextEnemy();
            enemy = enemy.updateSpellLayers();
            spellLayers = enemy.countLayersSpell();
            if (spellLayers >= towers.size() && towers.size() < 3) {
                    castSpell = false;
                    canCastSpell = false;
            } else {
                canCastSpell = true;
            }
            if (castSpell) {
                enemy = new SpellDecorator(enemy, multiplication);
            }

            for (Tower tower : towers) {
                double weatherAffectedDamage = weatherContext.damageChange(tower.getDamage()) * tower.getRate();
                if (tower.getRange() >= this.getDistance() - enemy.getDistance()) {
                    enemy.takeDamage((int) (weatherAffectedDamage));
                }
            }
            updatedEnemies.add((Enemy)enemy);
        }
        cli.printLine("Weather state this step: " + weatherContext.toString());
        weatherContext.changeWeatherState();
        this.enemies = updatedEnemies;
    }

    @Override
    public boolean anyEnemiesLeft() {
        for (Enemy enemy : enemies) {
            if (enemy.getHealth() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean anyEnemiesInRange() {
        for (Enemy enemy : enemies) {
            if (enemy.getHealth() > 0
                    && ((EnemyComponent) enemy).getDistance() + enemy.getSpeed() > getDistance()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prevent to retrieve the enemies from the round
     * 
     * @throws UnsupportedOperationException because we don't want to expose the enemies
     */
    @Override
    public List<Enemy> getEnemies() {
        throw new UnsupportedOperationException("getEnemies() is not supported");
    }
}
