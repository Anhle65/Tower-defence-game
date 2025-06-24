package uc.seng301.towerdefence.asg4.model;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "round")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_round")
    private Long roundId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private Player player;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_round")
    private List<Enemy> enemies;

    private int number;
    private int distance;

    /**
     * Default constructor for Round
     */
    public Round() {
        // a (public) constructor is needed by JPA
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Round (%d) - distance %dm%n", number, distance));
        return sb.toString();
    }

    /**
     * Add an enemy to the round
     *
     * @param enemy The enemy to add, cannot be null
     * @return true if the enemy was added, false if it was already in the list
     * @throws IllegalArgumentException If the enemy is null
     */
    public boolean addEnemy(Enemy enemy) {
        if (null == enemy) {
            throw new IllegalArgumentException("Enemy cannot be null");
        }
        return enemies.add(enemy);
    }

    /**
     * Remove an enemy from the round
     *
     * @param enemy The enemy to remove, cannot be null
     * @return true if the enemy was removed, false if it was not in the list
     * @throws IllegalArgumentException If the enemy is null
     * @throws IllegalStateException If the enemies list is null
     */
    public boolean removeEnemy(Enemy enemy) {
        if (null == enemy) {
            throw new IllegalArgumentException("Enemy cannot be null");
        }
        return enemies.remove(enemy);
    }

    /**
     * Notify the enemies of the round
     * 
     * @param towers the list of towers that will cause damage to the enemies
     * @throws UnsupportedOperationException because the round must be in a game to notify enemies
     */
    public void applyDamageToEnemies(List<Tower> towers) {
        throw new UnsupportedOperationException("The round must be in a game");
    }

    // JPA getter and setter methods - Javadoc omitted for brevity
    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }
}
