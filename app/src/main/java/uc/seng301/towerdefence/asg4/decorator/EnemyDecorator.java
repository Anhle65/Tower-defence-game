package uc.seng301.towerdefence.asg4.decorator;

import uc.seng301.towerdefence.asg4.model.Enemy;

public abstract class EnemyDecorator extends Enemy implements EnemyComponent {
    private final EnemyComponent wrappedEnemy;
    public EnemyDecorator(EnemyComponent enemy) {
        super();
        this.wrappedEnemy = enemy;
    }

    @Override
    public int getHealth() {
        return wrappedEnemy.getHealth();
    }

    @Override
    public void setHealth(int health) {
        wrappedEnemy.setHealth(health);
    }

    @Override
    public String getDisplayableHealth() {
        return wrappedEnemy.getDisplayableHealth();
    }

    @Override
    public String getName() {
        return wrappedEnemy.getName();
    }

    @Override
    public String getEmoji() {
        return wrappedEnemy.getEmoji();
    }

    @Override
    public void setEmoji(String emoji) {
        wrappedEnemy.setEmoji(emoji);
    }

    @Override
    public int getSpeed() {
        return wrappedEnemy.getSpeed();
    }

    @Override
    public void setSpeed(int speed) {
        wrappedEnemy.setSpeed(speed);
    }

    @Override
    public void takeDamage(int damage) {
        wrappedEnemy.takeDamage(damage);
    }

    @Override
    public int getDistance() {
        return wrappedEnemy.getDistance();
    }

    @Override
    public void updateDistance(int movedDistance) {
        wrappedEnemy.updateDistance(movedDistance);
    }

    @Override
    public EnemyComponent updateSpellLayers() {
        return wrappedEnemy.updateSpellLayers();
    }

}
