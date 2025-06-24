package uc.seng301.towerdefence.asg4.decorator;

import java.util.Random;

public class BoostingDamageRandomization {
    private static final int MAX_MULTIPLICATION = 50;
    private static final int MIN_MULTIPLICATION = 10;

    /**
     * Generates a random multiplication factor for boosting damage.
     *
     * @return a random integer between MIN_MULTIPLICATION and MAX_MULTIPLICATION (inclusive) (10% up to 50%).
     */
    public double generateRandomMultiplication() {
        return new Random().nextInt(MAX_MULTIPLICATION - MIN_MULTIPLICATION + 1) + MIN_MULTIPLICATION;
    }
}
