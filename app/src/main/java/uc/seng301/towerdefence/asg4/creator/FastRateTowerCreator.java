package uc.seng301.towerdefence.asg4.creator;

import uc.seng301.towerdefence.asg4.model.FastRateTower;

/**
 * FastRateTowerCreator is a concrete implementation of the TowerCreator class. It is used to create
 * a fast rate tower with a specified range and two times faster faster rate.
 */
public class FastRateTowerCreator extends TowerCreator {

    @Override
    public void makeNewTower(String towerName, int damage) {
        tower = new FastRateTower();
        tower.setName(towerName);
        tower.setDamage(damage);
    }

    @Override
    public void setTowerRange(int range) {
        tower.setRange(range);
    }

    /**
     * Sets the rate of the tower. The rate is doubled for fast rate towers.
     * 
     * @param rate the rate of the tower, will be double as fast for a fast rate tower
     */
    @Override
    public void setTowerRate(int rate) {
        tower.setRate(rate * 2);
    }
}
