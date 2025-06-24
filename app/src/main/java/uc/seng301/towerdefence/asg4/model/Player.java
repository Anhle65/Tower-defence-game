package uc.seng301.towerdefence.asg4.model;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_player")
    private Long playerId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private List<Round> rounds;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private List<Tower> towers;

    private String name;

    /**
     * Default constructor for Player
     */
    public Player() {
        // a (public) constructor is needed by JPA
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Player (%d): %s%n\tRounds:%n", playerId, name));
        for (Round round : rounds) {
            sb.append(String.format("\t\t%s%n\t\tEnemies%n", round.toString()));
            for (Enemy enemy : round.getEnemies()) {
                sb.append(String.format("\t\t\t\t%s%n", enemy.toString()));
            }
        }
        for (Tower tower : towers) {
            sb.append(String.format("\tTowers:%n\t\t%s%n", tower.toString()));
        }
        return sb.toString();
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public List<Tower> getTowers() {
        return towers;
    }
}
