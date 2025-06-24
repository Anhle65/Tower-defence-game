package uc.seng301.towerdefence.asg4.battle;

import java.util.Iterator;
import java.util.List;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Tower;

/**
 * Interface for handling battle rounds in the game
 * 
 * This interface defines the methods required to manage the battle round, including applying damage
 * to enemies, getting the next enemy, and checking if there are more enemies to traverse.
 */
public interface BattleHandler {

    /**
     * Enable to traverse the enemies in this round
     * 
     * @return an iterator of the enemies
     */
    Iterator<Enemy> getEnemyTraversal();

    /**
     * Get the next enemy in the round
     * 
     * @return the next enemy
     * @throws IllegalStateException If there are no more enemies to traverse
     */
    Enemy getNextEnemy() throws IllegalStateException;

    /**
     * Check if there are more enemies to traverse
     * 
     * @return true if there are more enemies, false otherwise
     */
    boolean hasNextEnemy();

    /**
     * Reset the enemy traversal
     */
    void resetEnemyTraversal();

    /**
     * Apply damage to the enemies in the round based on the towers
     * 
     * @param towers The towers to apply damage from
     */
    void applyDamageToEnemies(List<Tower> towers);

    /**
     * Check whether there are any enemies left in the round
     * 
     * @return true if there are enemies left, false otherwise
     */
    boolean anyEnemiesLeft();

    /**
     * Check whether there are any enemies in range of the tower
     * 
     * @return true if there are enemies in range, false otherwise
     */
    boolean anyEnemiesInRange();

}
