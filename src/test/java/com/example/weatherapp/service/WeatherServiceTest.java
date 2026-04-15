package com.example.weatherapp.service;

import com.example.weatherapp.client.OpenMeteoClient;
import com.example.weatherapp.model.GeocodingResponse;
import com.example.weatherapp.model.WeatherResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherServiceTest {

    @Test
    void shouldRejectBlankCity() {
        WeatherService weatherService = new WeatherService(new StubOpenMeteoClient());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getWeather("   ")
        );

        assertEquals("Devi inserire una citta valida.", exception.getMessage());
    }

    @Test
    void shouldReturnWeatherForFirstMatchingCity() throws IOException {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        client.setWeatherResponse(buildWeatherResponse());

        WeatherService weatherService = new WeatherService(client);
        WeatherResult weatherResult = weatherService.getWeather("  Roma  ");

        assertEquals("Roma", client.getRequestedCity());
        assertEquals(41.9028, client.getRequestedLatitude(), 0.0001);
        assertEquals(12.4964, client.getRequestedLongitude(), 0.0001);
        assertEquals("Roma", weatherResult.getCity());
        assertEquals("Italia", weatherResult.getCountry());
        assertEquals(13.0, weatherResult.getTemperature(), 0.0001);
        assertEquals("°C", weatherResult.getTemperatureUnit());
        assertEquals(12.4, weatherResult.getApparentTemperature(), 0.0001);
        assertEquals(0.6, weatherResult.getPrecipitation(), 0.0001);
        assertEquals(0.4, weatherResult.getRain(), 0.0001);
        assertEquals(0.2, weatherResult.getShowers(), 0.0001);
        assertEquals(15.0, weatherResult.getCloudCover(), 0.0001);
        assertEquals(8.7, weatherResult.getWindSpeed(), 0.0001);
        assertEquals("Sole pieno", weatherResult.getConditionDescription());
        assertEquals(WeatherVisualType.CLEAR, weatherResult.getVisualType());
        assertEquals("06:15", weatherResult.getSunrise());
        assertEquals("19:45", weatherResult.getSunset());
        assertEquals(8.0, weatherResult.getSunshineDurationHours(), 0.0001);
    }

    @Test
    void shouldRejectNullCity() {
        WeatherService weatherService = new WeatherService(new StubOpenMeteoClient());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getWeather(null)
        );

        assertEquals("Devi inserire una citta valida.", exception.getMessage());
    }

    @Test
    void shouldPropagateIOExceptionFromGeocode() {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodeException(new IOException("timeout di rete"));

        WeatherService weatherService = new WeatherService(client);

        assertThrows(IOException.class, () -> weatherService.getWeather("Roma"));
    }

    @Test
    void shouldPropagateIOExceptionFromFetchWeather() {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        client.setFetchWeatherException(new IOException("timeout lettura"));

        WeatherService weatherService = new WeatherService(client);

        assertThrows(IOException.class, () -> weatherService.getWeather("Roma"));
    }

    @Test
    void shouldThrowWhenCurrentIsNull() {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        WeatherResponse incompleteResponse = new WeatherResponse();
        incompleteResponse.setCurrent(null);
        incompleteResponse.setCurrentUnits(new WeatherResponse.CurrentUnits());
        client.setWeatherResponse(incompleteResponse);

        WeatherService weatherService = new WeatherService(client);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> weatherService.getWeather("Roma")
        );
        assertEquals("Risposta meteo incompleta da Open-Meteo.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenCurrentUnitsIsNull() {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        WeatherResponse incompleteResponse = new WeatherResponse();
        incompleteResponse.setCurrent(new WeatherResponse.Current());
        incompleteResponse.setCurrentUnits(null);
        client.setWeatherResponse(incompleteResponse);

        WeatherService weatherService = new WeatherService(client);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> weatherService.getWeather("Roma")
        );
        assertEquals("Risposta meteo incompleta da Open-Meteo.", exception.getMessage());
    }

    @Test
    void shouldUseNightDescriptionWhenIsDayIsZero() throws IOException {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        WeatherResponse weatherResponse = buildWeatherResponse();
        weatherResponse.getCurrent().setIsDay(0);
        weatherResponse.getCurrent().setWeatherCode(0);
        client.setWeatherResponse(weatherResponse);

        WeatherService weatherService = new WeatherService(client);
        WeatherResult result = weatherService.getWeather("Roma");

        assertEquals("Cielo sereno", result.getConditionDescription());
        assertFalse(result.isDay());
    }

    @Test
    void shouldHandleNullDailyData() throws IOException {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        client.setGeocodingResponse(buildGeocodingResponse("Roma", "Italia", 41.9028, 12.4964));
        WeatherResponse weatherResponse = buildWeatherResponse();
        weatherResponse.setDaily(null);
        client.setWeatherResponse(weatherResponse);

        WeatherService weatherService = new WeatherService(client);
        WeatherResult result = weatherService.getWeather("Roma");

        assertNull(result.getSunrise());
        assertNull(result.getSunset());
        assertNull(result.getSunshineDurationHours());
    }

    @Test
    void shouldFailWhenCityIsNotFound() {
        StubOpenMeteoClient client = new StubOpenMeteoClient();
        GeocodingResponse geocodingResponse = new GeocodingResponse();
        geocodingResponse.setResults(Collections.<GeocodingResponse.Result>emptyList());
        client.setGeocodingResponse(geocodingResponse);

        WeatherService weatherService = new WeatherService(client);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getWeather("Atlantide")
        );

        assertEquals("Citta non trovata: Atlantide", exception.getMessage());
    }

    private GeocodingResponse buildGeocodingResponse(String city, String country, double latitude, double longitude) {
        GeocodingResponse.Result result = new GeocodingResponse.Result();
        result.setName(city);
        result.setCountry(country);
        result.setLatitude(latitude);
        result.setLongitude(longitude);

        GeocodingResponse geocodingResponse = new GeocodingResponse();
        geocodingResponse.setResults(Collections.singletonList(result));
        return geocodingResponse;
    }

    private WeatherResponse buildWeatherResponse() {
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setTemperature2m(13.0);
        current.setApparentTemperature(12.4);
        current.setPrecipitation(0.6);
        current.setRain(0.4);
        current.setShowers(0.2);
        current.setSnowfall(0.0);
        current.setCloudCover(15.0);
        current.setWindSpeed10m(8.7);
        current.setIsDay(1);
        current.setWeatherCode(0);

        WeatherResponse.CurrentUnits currentUnits = new WeatherResponse.CurrentUnits();
        currentUnits.setTemperature2m("°C");
        currentUnits.setApparentTemperature("°C");
        currentUnits.setPrecipitation("mm");
        currentUnits.setRain("mm");
        currentUnits.setShowers("mm");
        currentUnits.setSnowfall("cm");
        currentUnits.setCloudCover("%");
        currentUnits.setWindSpeed10m("km/h");

        WeatherResponse.Daily daily = new WeatherResponse.Daily();
        daily.setSunrise(Collections.singletonList("2026-04-02T06:15"));
        daily.setSunset(Collections.singletonList("2026-04-02T19:45"));
        daily.setSunshineDuration(Collections.singletonList(28800.0));

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCurrent(current);
        weatherResponse.setCurrentUnits(currentUnits);
        weatherResponse.setDaily(daily);
        return weatherResponse;
    }

    private static class StubOpenMeteoClient extends OpenMeteoClient {
        private GeocodingResponse geocodingResponse;
        private WeatherResponse weatherResponse;
        private IOException geocodeException;
        private IOException fetchWeatherException;
        private String requestedCity;
        private double requestedLatitude;
        private double requestedLongitude;

        @Override
        public GeocodingResponse geocode(String city) throws IOException {
            requestedCity = city;
            if (geocodeException != null) throw geocodeException;
            return geocodingResponse;
        }

        @Override
        public WeatherResponse fetchCurrentWeather(double latitude, double longitude) throws IOException {
            requestedLatitude = latitude;
            requestedLongitude = longitude;
            if (fetchWeatherException != null) throw fetchWeatherException;
            return weatherResponse;
        }

        public void setGeocodingResponse(GeocodingResponse geocodingResponse) {
            this.geocodingResponse = geocodingResponse;
        }

        public void setWeatherResponse(WeatherResponse weatherResponse) {
            this.weatherResponse = weatherResponse;
        }

        public void setGeocodeException(IOException geocodeException) {
            this.geocodeException = geocodeException;
        }

        public void setFetchWeatherException(IOException fetchWeatherException) {
            this.fetchWeatherException = fetchWeatherException;
        }

        public String getRequestedCity() {
            return requestedCity;
        }

        public double getRequestedLatitude() {
            return requestedLatitude;
        }

        public double getRequestedLongitude() {
            return requestedLongitude;
        }
    }
}
