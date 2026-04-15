package com.example.weatherapp.service;

public class WeatherCondition {

    private final String description;
    private final WeatherVisualType visualType;

    public WeatherCondition(String description, WeatherVisualType visualType) {
        this.description = description;
        this.visualType = visualType;
    }

    public String getDescription() {
        return description;
    }

    public WeatherVisualType getVisualType() {
        return visualType;
    }
}
