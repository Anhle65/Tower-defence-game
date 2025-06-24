package uc.seng301.towerdefence.asg4.smoketests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uc.seng301.towerdefence.asg4.App;
import uc.seng301.towerdefence.asg4.Game;
import uc.seng301.towerdefence.asg4.accessor.PlayerAccessor;

class AppTest {

    @Test
    void testSimpleGameRound() {
        Game game = new App().getGame();
        game.createPlayer("create_player m");
        game.createRound("create_round m 1 110");
        game.createTower("create_tower m \"Ice Tower\" 6 basictower");
        game.createTower("create_tower m \"Fire Tower\" 5 fastratetower");
        game.createTower("create_tower m \"Sky Tower\" 5 longrangetower");
        game.printPlayer("print m");
        game.addEnemies("add_enemies m 1 5");
        PlayerAccessor playerAccessor = game.getPlayerAccessor();
        Assertions.assertEquals("m", playerAccessor.getPlayerByName("m").getName());
        Assertions.assertEquals(3, playerAccessor.getPlayerByName("m").getTowers().size());
        Assertions.assertEquals(1, playerAccessor.getPlayerByName("m").getRounds().size());
        Assertions.assertEquals(5,
                playerAccessor.getPlayerByName("m").getRounds().getFirst().getEnemies().size());
        game.playRound("play_round m 1");
        game.exit();
    }
}
