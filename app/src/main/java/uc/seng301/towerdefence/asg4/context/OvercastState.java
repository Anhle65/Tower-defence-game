package uc.seng301.towerdefence.asg4.context;

public class OvercastState implements WeatherState {
    private WeatherContext weatherContext;
    public void setWeatherContext(WeatherContext weatherContext) {
        this.weatherContext = weatherContext;
    }
    @Override
    public double damageChange(double damage) {
        return damage;
    }

    public OvercastState() {
    }
    public String toString() {
        return "Overcast";
    }
}
