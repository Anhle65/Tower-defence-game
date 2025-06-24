package uc.seng301.towerdefence.asg4.enemies;


import uc.seng301.towerdefence.asg4.model.Enemy;

/**
 * Card generation proxy for getting random card from API
 */
public class EnemyProxy implements EnemyGenerator {
    private final EnemyService enemyService;

    /**
     * Create a new Card proxy using the {@link EnemyService} implementation
     */
    public EnemyProxy() {
        this.enemyService = new EnemyService();
    }

    @Override
    public Enemy getRandomEnemy() {
        return enemyService.getRandomEnemy();
    }
}
