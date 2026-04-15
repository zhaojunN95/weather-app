package com.example.weatherapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherResultFormatterTest {

    @Test
    void shouldFormatTemperatureWithDegreeSymbol() {
        WeatherResult weatherResult = buildResult("Roma", "Italia", "06:20", "19:50", 8.4);

        assertEquals(
            "Temperatura attuale a Roma, Italia: 13,5 °C" + System.lineSeparator()
                + "Condizioni: Prevalentemente soleggiato" + System.lineSeparator()
                + "Percepita: 12,9 °C" + System.lineSeparator()
                + "Pioggia: 0,8 mm | Precipitazioni: 1,1 mm" + System.lineSeparator()
                + "Nuvolosita: 18,0 % | Vento: 9,3 km/h" + System.lineSeparator()
                + "Sole: alba 06:20 | tramonto 19:50 | 8,4 h di sole",
            WeatherResultFormatter.format(weatherResult)
        );
    }

    @Test
    void shouldOmitCountryWhenNull() {
        WeatherResult result = buildResult("Roma", null, "06:20", "19:50", 8.4);
        String formatted = WeatherResultFormatter.format(result);
        assertTrue(formatted.startsWith("Temperatura attuale a Roma: "));
    }

    @Test
    void shouldOmitCountryWhenBlank() {
        WeatherResult result = buildResult("Roma", "   ", "06:20", "19:50", 8.4);
        String formatted = WeatherResultFormatter.format(result);
        assertTrue(formatted.startsWith("Temperatura attuale a Roma: "));
    }

    @Test
    void shouldOmitSunLineWhenAllSunDataIsNull() {
        WeatherResult result = buildResult("Roma", "Italia", null, null, null);
        String formatted = WeatherResultFormatter.format(result);
        assertFalse(formatted.contains("Sole:"));
    }

    @Test
    void shouldFormatSunLineWithOnlySunrise() {
        WeatherResult result = buildResult("Roma", "Italia", "06:20", null, null);
        String formatted = WeatherResultFormatter.format(result);
        assertTrue(formatted.contains("Sole: alba 06:20"));
        assertFalse(formatted.contains("tramonto"));
        assertFalse(formatted.contains("di sole"));
    }

    @Test
    void shouldFormatSunLineWithOnlySunset() {
        WeatherResult result = buildResult("Roma", "Italia", null, "19:50", null);
        String formatted = WeatherResultFormatter.format(result);
        assertTrue(formatted.contains("Sole: tramonto 19:50"));
        assertFalse(formatted.contains("alba"));
        assertFalse(formatted.contains("di sole"));
    }

    @Test
    void shouldFormatSunLineWithOnlyDuration() {
        WeatherResult result = buildResult("Roma", "Italia", null, null, 6.5);
        String formatted = WeatherResultFormatter.format(result);
        assertTrue(formatted.contains("Sole: 6,5 h di sole"));
        assertFalse(formatted.contains("alba"));
        assertFalse(formatted.contains("tramonto"));
    }

    private WeatherResult buildResult(String city, String country, String sunrise, String sunset, Double sunshineDurationHours) {
        return WeatherResult.builder()
            .city(city)
            .country(country)
            .temperature(13.5)
            .temperatureUnit("°C")
            .apparentTemperature(12.9)
            .apparentTemperatureUnit("°C")
            .precipitation(1.1)
            .precipitationUnit("mm")
            .rain(0.8)
            .rainUnit("mm")
            .showers(0.3)
            .showersUnit("mm")
            .snowfall(0.0)
            .snowfallUnit("cm")
            .cloudCover(18.0)
            .cloudCoverUnit("%")
            .windSpeed(9.3)
            .windSpeedUnit("km/h")
            .day(true)
            .weatherCode(1)
            .conditionDescription("Prevalentemente soleggiato")
            .visualType(WeatherVisualType.PARTLY_CLOUDY)
            .sunrise(sunrise)
            .sunset(sunset)
            .sunshineDurationHours(sunshineDurationHours)
            .sunshineDurationUnit("h")
            .build();
    }
}
