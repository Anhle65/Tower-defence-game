package uc.seng301.towerdefence.asg4.unittests.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uc.seng301.towerdefence.asg4.model.Player;

class PlayerTest {
    @Test
    void createPlayer() {
        Player player = new Player();
        player.setPlayerId(1L);
        player.setName("Ogre");

        Assertions.assertEquals(1L, player.getPlayerId());
        Assertions.assertEquals("Ogre", player.getName());
    }
}
