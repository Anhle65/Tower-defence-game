package uc.seng301.towerdefence.asg4.battle;

import uc.seng301.towerdefence.asg4.decorator.EnemyComponent;
import uc.seng301.towerdefence.asg4.model.Enemy;

public class BattleEnemy extends Enemy implements EnemyComponent {
    private int distance;
    public BattleEnemy(Enemy enemy) {
        setRound(enemy.getRound());
        setEnemyId(enemy.getEnemyId());
        setName(enemy.getName());
        setSpeed(enemy.getSpeed());
        setEmoji(enemy.getEmoji());
        setHealth(enemy.getHealth());
        this.distance = 0;
    }
    public BattleEnemy(){}

    /**
     * Update the enemy's health by subtracting the damage taken
     * 
     * @param damage The amount of damage taken
     * @throws IllegalArgumentException If the damage is negative
     */
    @Override
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        if (getHealth() > 0) {
            setHealth(getHealth() - damage);
        }
    }
    /**
     * @return the distance where this enemy stands in a running round
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Update the distance of the enemy. The distance is decreased by the moved distance.
     * 
     * @param movedDistance the distance where this enemy stands in a running round
     */
    public void updateDistance(int movedDistance) {
        if (movedDistance < 0) {
            throw new IllegalArgumentException("Moved distance cannot be negative");
        }
        this.distance += movedDistance;
    }

    /**
     * This return the base Enemy without any spell layers applied.
     *
     * @return unwrapped enemy
     */
    public EnemyComponent updateSpellLayers() {
        return this;
    }

    /**
     * Count the number of spell layers applied to this enemy which is always 0 for the base enemy.
     *
     * @return the number of spell layers
     */
    public int countLayersSpell() {
        return 0;
    }
}
