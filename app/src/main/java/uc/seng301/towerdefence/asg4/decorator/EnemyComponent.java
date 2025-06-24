package uc.seng301.towerdefence.asg4.decorator;

public interface EnemyComponent {

    int getHealth();

    void setHealth(int health);

    String getDisplayableHealth();

    String getName();

    String getEmoji();

    void setEmoji(String emoji);

    int getSpeed();

    void setSpeed(int speed);

    void takeDamage(int damage);

    int getDistance();

    void updateDistance(int movedDistance);

    /**
     * This return the inner layer(s).
     * @return unwrapped enemy
     */
    EnemyComponent updateSpellLayers();

    /**
     * This count the number of layers of spell currently applied to the enemy.
     * @return the number of layers
     */
    int countLayersSpell();
}
