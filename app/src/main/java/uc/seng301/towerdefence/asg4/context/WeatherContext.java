package uc.seng301.towerdefence.asg4.context;

import java.util.List;
import java.util.Random;

public class WeatherContext implements WeatherState {
    private WeatherState weatherState;
    private static final List<WeatherState> AVAILABLE_WEATHER_STATES = List.of(
        new OvercastState(),
        new SunnyState(),
        new ColdState()
    );
    private final Random random = new Random();

    /**
     * Default constructor that initializes the overcast weather state to the first step in a round.
     */
    public WeatherContext() {
        this.weatherState = AVAILABLE_WEATHER_STATES.getFirst();
    }

    public WeatherContext(WeatherState weatherState) {
        this.weatherState = weatherState;
    }

    @Override
    public double damageChange(double damage) {
        return this.weatherState.damageChange(damage);
    }

    /**
     * Changes the current weather state to a random one from the available weather states.
     */
    public void changeWeatherState() {
        this.weatherState = AVAILABLE_WEATHER_STATES.get(random.nextInt(AVAILABLE_WEATHER_STATES.size()));
    }

    @Override
    public String toString(){
        return weatherState.toString();
    }
}
