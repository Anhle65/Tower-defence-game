package uc.seng301.towerdefence.asg4.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enemy")
public class Enemy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_enemy")
    private Long enemyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_round")
    private Round round;

    private String name;
    private int speed;
    private int health;
    private String emoji;

    /**
     * Default constructor for Enemy
     */
    public Enemy() {
        // a public constructor is needed by JPA
    }

    /**
     * Update the enemy's health by subtracting the damage taken
     *
     * @param damage The amount of damage taken
     * @throws UnsupportedOperationException because the enemy must be in a battle
     */
    public void takeDamage(int damage) {
        throw new UnsupportedOperationException("The enemy must be in a battle");
    }

    // JPA setters and getters - Javadoc omitted for brevity
    public Long getEnemyId() {
        return enemyId;
    }

    public void setEnemyId(Long enemyId) {
        this.enemyId = enemyId;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHealth() {
        return health;
    }

    public String getDisplayableHealth() {
        if (health <= 0) {
            return "0  ";
        }
        if (health < 10) {
            return health + "  ";
        }
        if (health <= 100) {
            return health + " ";
        }
        return health + "";
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String toString() {
        return String.format("Enemy (%d): %s %s -- speed: %d, health: %d", enemyId, emoji, name,
                speed, health);
    }
}
