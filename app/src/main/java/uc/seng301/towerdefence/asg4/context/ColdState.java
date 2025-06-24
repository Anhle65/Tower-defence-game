package uc.seng301.towerdefence.asg4.context;

public class ColdState implements WeatherState{
    private WeatherContext weatherContext;
    public void setWeatherContext(WeatherContext weatherContext) {
        this.weatherContext = weatherContext;
    }
    public ColdState() {}
    @Override
    public double damageChange(double damage) {
        return 0;
    }

    public String toString() {
        return "Cold";
    }
}
