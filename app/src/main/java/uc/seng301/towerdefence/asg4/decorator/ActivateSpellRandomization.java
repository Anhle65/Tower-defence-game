package uc.seng301.towerdefence.asg4.decorator;

import java.util.Random;

public class ActivateSpellRandomization {
    private int numberOfSpells = 0;
    /**
     * Generates a random activation number for spells.
     * The number is between 0 and 3 (25%).
     * @return a random integer between 0 and 3, or -1 if the number of spells in this round is already 3.
     */
    public int generateRandomActivation() {
        if( numberOfSpells >= 3) {
            return -1;
        }
        return new Random().nextInt(4);
    }

    /**
     * Increases the number of spells activated in this round.
     * This method will be called each time a spell is successfully activated.
     */
    public void increaseNumberOfSpells() {
        numberOfSpells++;
    }
}
