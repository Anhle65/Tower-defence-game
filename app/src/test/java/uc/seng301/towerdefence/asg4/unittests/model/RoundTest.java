package uc.seng301.towerdefence.asg4.unittests.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uc.seng301.towerdefence.asg4.model.Round;

class RoundTest {
    @Test
    void createRound() {
        Round round = new Round();
        round.setRoundId(1L);
        round.setNumber(1);
        round.setDistance(100);

        Assertions.assertEquals(1L, round.getRoundId());
        Assertions.assertEquals(1, round.getNumber());
        Assertions.assertEquals(100, round.getDistance());
    }
}
