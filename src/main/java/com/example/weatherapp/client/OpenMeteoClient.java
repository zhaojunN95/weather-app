package com.example.weatherapp.client;

import com.example.weatherapp.model.GeocodingResponse;
import com.example.weatherapp.model.WeatherResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class OpenMeteoClient {

    private static final Logger LOGGER = Logger.getLogger(OpenMeteoClient.class.getName());

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search?count=1&language=it&format=json&name=";
    private static final String FORECAST_URL =
        "https://api.open-meteo.com/v1/forecast"
            + "?current=temperature_2m,apparent_temperature,precipitation,rain,showers,snowfall,"
            + "cloud_cover,wind_speed_10m,relative_humidity_2m,is_day,weather_code"
            + "&daily=sunrise,sunset,sunshine_duration,"
            + "temperature_2m_max,temperature_2m_min,precipitation_sum,weather_code"
            + "&forecast_days=5"
            + "&timezone=auto";

    private final ObjectMapper objectMapper;

    public OpenMeteoClient() {
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GeocodingResponse geocode(String city) throws IOException {
        LOGGER.fine("Geocodifica per la citta: " + city);
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.name());
        String responseBody = get(GEOCODING_URL + encodedCity);
        return objectMapper.readValue(responseBody, GeocodingResponse.class);
    }

    /**
     * Recupera le condizioni meteorologiche correnti dall'API Open-Meteo
     * per le coordinate geografiche indicate.
     *
     * <p>La richiesta HTTP GET viene effettuata verso l'endpoint
     * {@code api.open-meteo.com/v1/forecast} con i seguenti parametri fissi:
     * temperatura a 2 m, temperatura apparente, precipitazioni, pioggia,
     * rovesci, nevicate, copertura nuvolosa, velocità del vento,
     * indicatore giorno/notte, codice meteo WMO, alba, tramonto e
     * durata del soleggiamento (forecast di 1 giorno con timezone automatico).
     *
     * <p><b>Esempio di utilizzo:</b>
     * <pre>{@code
     * OpenMeteoClient client = new OpenMeteoClient();
     * // Coordinate di Roma
     * WeatherResponse response = client.fetchCurrentWeather(41.9028, 12.4964);
     * double temp = response.getCurrent().getTemperature2m(); // es. 22.5
     * }</pre>
     *
     * @param latitude   latitudine geografica in gradi decimali
     *                   (range {@code -90.0} .. {@code +90.0})
     * @param longitude  longitudine geografica in gradi decimali
     *                   (range {@code -180.0} .. {@code +180.0})
     * @return {@link WeatherResponse} con i dati correnti e giornalieri
     *         deserializzati dalla risposta JSON dell'API
     * @throws IOException se si verifica un errore di connessione, un timeout
     *                     (connect/read timeout: 5 000 ms) o la risposta HTTP
     *                     indica un codice di errore (4xx / 5xx)
     * @see WeatherResponse
     */
    public WeatherResponse fetchCurrentWeather(double latitude, double longitude) throws IOException {
        LOGGER.fine("Recupero meteo per lat=" + latitude + " lon=" + longitude);
        String requestUrl = FORECAST_URL
            + "&latitude=" + latitude
            + "&longitude=" + longitude;

        String responseBody = get(requestUrl);
        return objectMapper.readValue(responseBody, WeatherResponse.class);
    }

    private String get(String urlString) throws IOException {
        LOGGER.fine("HTTP GET: " + urlString);
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try {
            int statusCode = connection.getResponseCode();
            LOGGER.fine("HTTP " + statusCode + " per: " + urlString);
            InputStream stream = statusCode >= 200 && statusCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();

            if (stream == null) {
                throw new IOException("Risposta HTTP non valida: " + statusCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                if (statusCode < 200 || statusCode >= 300) {
                    throw new IOException("HTTP " + statusCode + ": " + response);
                }

                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }
}
