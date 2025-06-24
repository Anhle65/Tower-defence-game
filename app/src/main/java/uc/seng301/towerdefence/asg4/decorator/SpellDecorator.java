package uc.seng301.towerdefence.asg4.decorator;

public class SpellDecorator extends EnemyDecorator {
    private final double multiplication;
    private final EnemyComponent enemy;
    private int remainingStepForSpell;

    public SpellDecorator(EnemyComponent enemy, double multiplication) {
        super(enemy);
        this.enemy = enemy;
        this.multiplication = multiplication;
        this.remainingStepForSpell = 3;
    }

    @Override
    public int getHealth() {
        return enemy.getHealth();
    }

    @Override
    public void takeDamage(int damage) {
        if (remainingStepForSpell > 0) {
            applySpell(damage);
        } else {
            enemy.takeDamage(damage);
        }
    }

    /**
     * This method applies the spell effect to the enemy.
     * It boosts the damage taken by the enemy based on the multiplication factor.
     *
     * @param damage The base damage to be applied to the enemy.
     */
    private void applySpell(int damage) {
        double boostedDamage = damage * (1 + multiplication / 100);
        enemy.takeDamage((int) boostedDamage);
    }

    /**
     * This return the enemy without expired wrap.
     * @return unwrapped enemy
     */
    @Override
    public EnemyComponent updateSpellLayers() {
        EnemyComponent updatedInner = enemy.updateSpellLayers();
        remainingStepForSpell--;
        if (remainingStepForSpell == 0) {
            return updatedInner;
        }
        return this;
    }

    /**
     * This count the number of layers of spell currently applied to the enemy.
     * @return the number of layers
     */
    @Override
    public int countLayersSpell() {
        int count = remainingStepForSpell > 0 ? 1 : 0;
        if (enemy instanceof EnemyComponent wrappedEnemy) {
            count += wrappedEnemy.countLayersSpell();
        }
        return count;
    }
}