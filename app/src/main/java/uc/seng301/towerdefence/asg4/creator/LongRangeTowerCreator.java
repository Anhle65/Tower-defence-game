package uc.seng301.towerdefence.asg4.creator;

import uc.seng301.towerdefence.asg4.model.LongRangeTower;

/**
 * LongRangeTowerCreator is a concrete implementation of the TowerCreator class. It is used to
 * create a long range tower with a specified range (doubled, so longer) and rate.
 */
public class LongRangeTowerCreator extends TowerCreator {

    @Override
    public void makeNewTower(String towerName, int damage) {
        tower = new LongRangeTower();
        tower.setName(towerName);
        tower.setDamage(damage);
    }

    /**
     * Sets the range of the tower. The range is doubled for long range towers.
     * 
     * @param tower a Tower object
     * @param range the range of the tower, will be doubled for a long range tower
     * @return the Tower object with the given range
     */
    @Override
    public void setTowerRange(int range) {
        tower.setRange(range * 2);
    }

    @Override
    public void setTowerRate(int rate) {
        tower.setRate(rate);
    }

}
