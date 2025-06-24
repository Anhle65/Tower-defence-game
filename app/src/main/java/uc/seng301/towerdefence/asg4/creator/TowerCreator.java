package uc.seng301.towerdefence.asg4.creator;


import uc.seng301.towerdefence.asg4.model.Tower;

/**
 * TowerCreator is an abstract class that defines the methods for creating a tower. It is used to
 * create different types of towers.
 */
public abstract class TowerCreator {

    Tower tower;

    /**
     * Creates a tower with the given name and damage.
     * 
     * @param towerName a name of the tower
     * @param damage the damage of the tower
     */
    public abstract void makeNewTower(String towerName, int damage);

    /**
     * Sets the range of the tower.
     * 
     * @param range the range of the tower
     */
    public abstract void setTowerRange(int range);

    /**
     * Sets the rate of the tower.
     * 
     * @param rate the rate of the tower
     */
    public abstract void setTowerRate(int rate);

    /**
     * Returns the tower.
     * 
     * @return the Tower object that was created
     */
    public Tower getTower() {
        return tower;
    }
}
