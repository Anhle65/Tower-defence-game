package uc.seng301.towerdefence.asg4.context;

public interface WeatherState {

    /**
     * Calculates the change in damage based on the current weather state.
     * @param damage
     * @return new damage value after applying the weather effect
     */
    double damageChange(double damage);
    /**
     * Returns a string representation of the current weather state.
     * @return A string describing the current weather state.
     */
    String toString();
}
