package com.example.weatherapp.service;

import java.util.Locale;

public final class WeatherResultFormatter {

    private static final Locale OUTPUT_LOCALE = Locale.ITALY;

    private WeatherResultFormatter() {
    }

    public static String format(WeatherResult weatherResult) {
        StringBuilder output = new StringBuilder();

        output.append("Temperatura attuale a ")
            .append(weatherResult.getCity());

        if (weatherResult.getCountry() != null && !weatherResult.getCountry().trim().isEmpty()) {
            output.append(", ").append(weatherResult.getCountry());
        }

        output.append(": ")
            .append(String.format(OUTPUT_LOCALE, "%.1f", weatherResult.getTemperature()))
            .append(' ')
            .append(weatherResult.getTemperatureUnit())
            .append(System.lineSeparator())
            .append("Condizioni: ")
            .append(weatherResult.getConditionDescription())
            .append(System.lineSeparator())
            .append("Percepita: ")
            .append(formatValue(weatherResult.getApparentTemperature(), weatherResult.getApparentTemperatureUnit()))
            .append(System.lineSeparator())
            .append("Pioggia: ")
            .append(formatValue(weatherResult.getRain(), weatherResult.getRainUnit()))
            .append(" | Precipitazioni: ")
            .append(formatValue(weatherResult.getPrecipitation(), weatherResult.getPrecipitationUnit()))
            .append(System.lineSeparator())
            .append("Nuvolosita: ")
            .append(formatValue(weatherResult.getCloudCover(), weatherResult.getCloudCoverUnit()))
            .append(" | Vento: ")
            .append(formatValue(weatherResult.getWindSpeed(), weatherResult.getWindSpeedUnit()));

        if (weatherResult.getHumidityUnit() != null && !weatherResult.getHumidityUnit().trim().isEmpty()) {
            output.append(System.lineSeparator())
                .append("Umidita: ")
                .append(weatherResult.getHumidity())
                .append(' ')
                .append(weatherResult.getHumidityUnit());
        }

        if (weatherResult.getSunrise() != null || weatherResult.getSunset() != null || weatherResult.getSunshineDurationHours() != null) {
            output.append(System.lineSeparator())
                .append("Sole: ")
                .append(formatSun(weatherResult));
        }

        return output.toString();
    }

    private static String formatValue(double value, String unit) {
        return String.format(OUTPUT_LOCALE, "%.1f %s", value, unit);
    }

    private static String formatSun(WeatherResult weatherResult) {
        StringBuilder value = new StringBuilder();

        if (weatherResult.getSunrise() != null) {
            value.append("alba ").append(weatherResult.getSunrise());
        }

        if (weatherResult.getSunset() != null) {
            if (value.length() > 0) {
                value.append(" | ");
            }
            value.append("tramonto ").append(weatherResult.getSunset());
        }

        if (weatherResult.getSunshineDurationHours() != null) {
            if (value.length() > 0) {
                value.append(" | ");
            }
            value.append(String.format(
                OUTPUT_LOCALE,
                "%.1f %s di sole",
                weatherResult.getSunshineDurationHours(),
                weatherResult.getSunshineDurationUnit()
            ));
        }

        return value.toString();
    }
}
