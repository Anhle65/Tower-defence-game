package uc.seng301.towerdefence.asg4.enemies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uc.seng301.towerdefence.asg4.model.Enemy;

/**
 * Card
 */
public class EnemyResponse {
    @JsonDeserialize
    @JsonProperty("name")
    private String name;

    @JsonDeserialize
    @JsonProperty("health")
    private int health;

    @JsonDeserialize
    @JsonProperty("speed")
    private int speed;

    @JsonDeserialize
    @JsonProperty("emoji")
    private String emoji;

    public EnemyResponse() {
        // no-args jackson constructor
    }

    public Enemy toEnemy() {
        Enemy enemy = new Enemy();
        enemy.setName(name);
        enemy.setSpeed(speed);
        enemy.setHealth(health);
        enemy.setEmoji(emoji);
        return enemy;
    }
}
