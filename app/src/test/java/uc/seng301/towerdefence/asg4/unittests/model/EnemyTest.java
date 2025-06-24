package uc.seng301.towerdefence.asg4.unittests.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uc.seng301.towerdefence.asg4.model.Enemy;

class EnemyTest {
    @Test
    void createEnemy() {
        Enemy enemy = new Enemy();
        enemy.setEnemyId(1L);
        enemy.setName("Ogre");
        enemy.setSpeed(10);
        enemy.setHealth(100);

        Assertions.assertEquals(1L, enemy.getEnemyId());
        Assertions.assertEquals("Ogre", enemy.getName());
        Assertions.assertEquals(10, enemy.getSpeed());
        Assertions.assertEquals(100, enemy.getHealth());
    }
}
