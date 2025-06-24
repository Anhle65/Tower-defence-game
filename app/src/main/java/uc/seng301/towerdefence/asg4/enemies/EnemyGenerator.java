package uc.seng301.towerdefence.asg4.enemies;


import uc.seng301.towerdefence.asg4.model.Enemy;

/**
 * Enemy generation interface
 */
public interface EnemyGenerator {
    /**
     * Get a random enemy
     * 
     * @return a randomly generated enemy
     */
    Enemy getRandomEnemy();
}
