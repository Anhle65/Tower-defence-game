package uc.seng301.towerdefence.asg4.creator;

import uc.seng301.towerdefence.asg4.model.BasicTower;

/**
 * BasicTowerCreator is a concrete implementation of the TowerCreator class. It is used to create a
 * basic tower with a specified range and rate.
 */
public class BasicTowerCreator extends TowerCreator {

    public void makeNewTower(String towerName, int damage) {
        tower = new BasicTower();
        tower.setName(towerName);
        tower.setDamage(damage);
    }

    @Override
    public void setTowerRange(int range) {
        tower.setRange(range);
    }

    @Override
    public void setTowerRate(int rate) {
        tower.setRate(rate);
    }
}
