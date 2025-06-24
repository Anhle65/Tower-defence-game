package uc.seng301.towerdefence.asg4.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FastRateTower")
public class FastRateTower extends Tower {

    /**
     * Default constructor for FastRateTower. This constructor is needed by JPA to create instance
     * of this class
     */
    public FastRateTower() {
        // empty constructor for JPA
    }
}

