package uc.seng301.towerdefence.asg4.context;

public class SunnyState implements WeatherState {
    private WeatherContext weatherContext;
    public void setWeatherContext(WeatherContext weatherContext) {
        this.weatherContext = weatherContext;
    }
    public SunnyState() {
    }
    @Override
    public double damageChange(double damage) {
        return damage / 2;
    }
    public String toString() {
        return "Sunny";
    }
}
