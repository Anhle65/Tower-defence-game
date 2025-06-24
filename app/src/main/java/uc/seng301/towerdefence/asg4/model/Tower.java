package uc.seng301.towerdefence.asg4.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tower_type")
public abstract class Tower {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_tower")
    private Long towerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private Player player;

    private String name;
    private int damage;
    private int range;
    private int rate;

    @Override
    public String toString() {
        return String.format("Tower (%d - %s): %s -- damage: %d, rate: %d, range: %dm", towerId,
                getClass().getSimpleName(), name, damage, rate, range);
    }

    // Getters and Setters - Javadoc omitted for brevity
    public Long getTowerId() {
        return towerId;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRange() {
        return this.range;
    }

    public int getRate() {
        return this.rate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

}
