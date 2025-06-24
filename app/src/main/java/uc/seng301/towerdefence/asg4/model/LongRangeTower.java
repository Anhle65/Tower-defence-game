package uc.seng301.towerdefence.asg4.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LongRangeTower")
public class LongRangeTower extends Tower {

    /**
     * Default constructor for LongRangeTower. This constructor is needed by JPA to create instances
     * of this class
     */
    public LongRangeTower() {
        // empty constructor for JPA
    }
}
