package uc.seng301.towerdefence.asg4.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BasicTower")
public class BasicTower extends Tower {

    /**
     * Default constructor for BasicTower. This constructor is needed by JPA to create instances of
     * this class.
     */
    public BasicTower() {
        // empty constructor for JPA
    }

}
