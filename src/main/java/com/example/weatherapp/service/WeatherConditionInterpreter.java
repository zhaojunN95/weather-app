package com.example.weatherapp.service;

public final class WeatherConditionInterpreter {

    private WeatherConditionInterpreter() {
    }

    public static WeatherCondition interpret(int weatherCode, boolean isDay) {
        switch (weatherCode) {
            case 0:
                return new WeatherCondition(isDay ? "Sole pieno" : "Cielo sereno", WeatherVisualType.CLEAR);
            case 1:
                return new WeatherCondition(
                    isDay ? "Prevalentemente soleggiato" : "Prevalentemente sereno",
                    WeatherVisualType.PARTLY_CLOUDY
                );
            case 2:
                return new WeatherCondition("Parzialmente nuvoloso", WeatherVisualType.PARTLY_CLOUDY);
            case 3:
                return new WeatherCondition("Coperto", WeatherVisualType.CLOUDY);
            case 45:
            case 48:
                return new WeatherCondition("Nebbia", WeatherVisualType.FOG);
            case 51:
            case 53:
            case 55:
            case 56:
            case 57:
                return new WeatherCondition("Pioviggine", WeatherVisualType.DRIZZLE);
            case 61:
            case 63:
            case 65:
            case 66:
            case 67:
            case 80:
            case 81:
            case 82:
                return new WeatherCondition("Pioggia", WeatherVisualType.RAIN);
            case 71:
            case 73:
            case 75:
            case 77:
            case 85:
            case 86:
                return new WeatherCondition("Neve", WeatherVisualType.SNOW);
            case 95:
            case 96:
            case 99:
                return new WeatherCondition("Temporale", WeatherVisualType.THUNDERSTORM);
            default:
                return new WeatherCondition("Condizioni variabili", WeatherVisualType.CLOUDY);
        }
    }
}
