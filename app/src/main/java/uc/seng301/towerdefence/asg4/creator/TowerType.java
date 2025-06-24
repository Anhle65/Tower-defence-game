package uc.seng301.towerdefence.asg4.creator;

/**
 * Enum for the different types of towers
 */
public enum TowerType {
    /**
     * Enum for the different types of towers
     */
    BASIC_TOWER("BasicTower"), LONG_RANGE_TOWER("LongRangeTower"), FAST_RATE_TOWER("FastRateTower");

    private final String type;

    TowerType(String type) {
        this.type = type;
    }

    String getType() {
        return type;
    }

    /**
     * Get the TowerCreator for this tower type
     *
     * @return The TowerCreator for this tower type
     */
    public TowerCreator getTowerCreator() {
        switch (this) {
            case BASIC_TOWER:
                return new BasicTowerCreator();
            case LONG_RANGE_TOWER:
                return new LongRangeTowerCreator();
            case FAST_RATE_TOWER:
                return new FastRateTowerCreator();
            default:
                throw new IllegalArgumentException("Unknown tower type: " + this);
        }
    }

    /**
     * Helper method to get the TowerType from a string
     *
     * @param type The string to convert to a TowerType
     * @return The TowerType for the given string
     * @throws IllegalArgumentException If the string does not match any TowerType
     */

    public static TowerType fromString(String type) {
        for (TowerType towerType : TowerType.values()) {
            if (towerType.getType().equalsIgnoreCase(type)) {
                return towerType;
            }
        }
        throw new IllegalArgumentException("Unknown tower type: " + type);
    }
}
