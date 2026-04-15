package com.example.weatherapp.service;

import com.example.weatherapp.client.OpenMeteoClient;
import com.example.weatherapp.model.GeocodingResponse;
import com.example.weatherapp.model.WeatherResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class WeatherService {

    private static final Logger LOGGER = Logger.getLogger(WeatherService.class.getName());

    private static final DateTimeFormatter API_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter OUTPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ITALY);

    private final OpenMeteoClient openMeteoClient;
    private final WeatherCache cache;

    public WeatherService() {
        this(new OpenMeteoClient());
    }

    public WeatherService(OpenMeteoClient openMeteoClient) {
        this(openMeteoClient, new WeatherCache());
    }

    public WeatherService(OpenMeteoClient openMeteoClient, WeatherCache cache) {
        this.openMeteoClient = Objects.requireNonNull(openMeteoClient, "OpenMeteoClient obbligatorio.");
        this.cache = Objects.requireNonNull(cache, "WeatherCache obbligatorio.");
    }

    /**
     * Recupera le condizioni meteorologiche correnti per la città specificata,
     * inclusa la temperatura (effettiva e percepita), precipitazioni, vento,
     * copertura nuvolosa, alba/tramonto e durata del soleggiamento.
     *
     * <p>Il metodo esegue le seguenti operazioni in sequenza:
     * <ol>
     *   <li>Normalizza il nome della città (trim).</li>
     *   <li>Esegue la geocodifica tramite l'API Open-Meteo per ottenere
     *       latitudine e longitudine.</li>
     *   <li>Richiede i dati meteo correnti con le coordinate ottenute.</li>
     *   <li>Interpreta il codice WMO per produrre una descrizione leggibile
     *       della condizione atmosferica ({@link WeatherConditionInterpreter}).</li>
     *   <li>Restituisce un {@link WeatherResult} immutabile con tutti i dati.</li>
     * </ol>
     *
     * <p><b>Esempio di utilizzo:</b>
     * <pre>{@code
     * WeatherService service = new WeatherService();
     * try {
     *     WeatherResult result = service.getWeather("Roma");
     *     System.out.println("Temperatura: "
     *         + result.getTemperature()
     *         + result.getTemperatureUnit());          // es. "22.5°C"
     *     System.out.println("Percepita:   "
     *         + result.getApparentTemperature()
     *         + result.getApparentTemperatureUnit()); // es. "20.1°C"
     *     System.out.println("Condizione:  "
     *         + result.getConditionDescription());    // es. "Sereno"
     * } catch (IllegalArgumentException e) {
     *     System.err.println("Città non trovata: " + e.getMessage());
     * } catch (IOException e) {
     *     System.err.println("Errore di rete: " + e.getMessage());
     * }
     * }</pre>
     *
     * @param city  nome della città di cui si vuole conoscere il meteo;
     *              non deve essere {@code null} né vuoto
     * @return      {@link WeatherResult} con temperatura, precipitazioni, vento,
     *              copertura nuvolosa, alba/tramonto e tipo visivo dell'icona
     * @throws IllegalArgumentException se {@code city} è {@code null}, vuota
     *                                  o non viene trovata tramite geocodifica
     * @throws IllegalStateException    se la risposta dell'API Open-Meteo
     *                                  è incompleta o malformata
     * @throws IOException              in caso di errore di rete o HTTP
     * @see WeatherResult
     * @see WeatherConditionInterpreter
     */
    public WeatherResult getWeather(String city) throws IOException {
        LOGGER.fine("Richiesta meteo per la citta: " + city);
        String normalizedCity = normalizeCity(city);
        LOGGER.fine("Citta normalizzata: '" + normalizedCity + "' — controllo cache");

        WeatherResult cached = cache.get(normalizedCity);
        if (cached != null) {
            LOGGER.fine("Cache hit per: " + normalizedCity);
            return WeatherResult.builder()
                    .city(cached.getCity())
                    .country(cached.getCountry())
                    .temperature(cached.getTemperature())
                    .temperatureUnit(cached.getTemperatureUnit())
                    .apparentTemperature(cached.getApparentTemperature())
                    .apparentTemperatureUnit(cached.getApparentTemperatureUnit())
                    .precipitation(cached.getPrecipitation())
                    .precipitationUnit(cached.getPrecipitationUnit())
                    .rain(cached.getRain())
                    .rainUnit(cached.getRainUnit())
                    .showers(cached.getShowers())
                    .showersUnit(cached.getShowersUnit())
                    .snowfall(cached.getSnowfall())
                    .snowfallUnit(cached.getSnowfallUnit())
                    .cloudCover(cached.getCloudCover())
                    .cloudCoverUnit(cached.getCloudCoverUnit())
                    .windSpeed(cached.getWindSpeed())
                    .windSpeedUnit(cached.getWindSpeedUnit())
                    .humidity(cached.getHumidity())
                    .humidityUnit(cached.getHumidityUnit())
                    .day(cached.isDay())
                    .weatherCode(cached.getWeatherCode())
                    .conditionDescription(cached.getConditionDescription())
                    .visualType(cached.getVisualType())
                    .sunrise(cached.getSunrise())
                    .sunset(cached.getSunset())
                    .sunshineDurationHours(cached.getSunshineDurationHours())
                    .sunshineDurationUnit(cached.getSunshineDurationUnit())
                    .forecast(cached.getForecast())
                    .fromCache(true)
                    .build();
        }

        LOGGER.fine("Cache miss — avvio geocodifica per: " + normalizedCity);

        GeocodingResponse geocodingResponse = openMeteoClient.geocode(normalizedCity);
        List<GeocodingResponse.Result> results = geocodingResponse.getResults();

        if (results == null || results.isEmpty()) {
            LOGGER.warning("Nessun risultato di geocodifica per: " + normalizedCity);
            throw new IllegalArgumentException("Citta non trovata: " + normalizedCity);
        }

        LOGGER.fine("Trovati " + results.size() + " risultati per: " + normalizedCity);
        GeocodingResponse.Result location = results.get(0);
        LOGGER.fine("Posizione selezionata: " + location.getName() + " (" + location.getCountry()
            + ") lat=" + location.getLatitude() + " lon=" + location.getLongitude());

        WeatherResponse weatherResponse = openMeteoClient.fetchCurrentWeather(
            location.getLatitude(),
            location.getLongitude()
        );

        if (weatherResponse.getCurrent() == null || weatherResponse.getCurrentUnits() == null) {
            LOGGER.warning("Risposta meteo incompleta per: " + normalizedCity);
            throw new IllegalStateException("Risposta meteo incompleta da Open-Meteo.");
        }

        LOGGER.fine("Dati meteo ricevuti per: " + location.getName());

        WeatherResponse.Current current = weatherResponse.getCurrent();
        WeatherResponse.CurrentUnits currentUnits = weatherResponse.getCurrentUnits();
        boolean isDay = current.getIsDay() == 1;
        WeatherCondition weatherCondition = WeatherConditionInterpreter.interpret(current.getWeatherCode(), isDay);

        String tempUnit = currentUnits.getTemperature2m() != null ? currentUnits.getTemperature2m() : "°C";
        WeatherResponse.Daily daily = weatherResponse.getDaily();

        WeatherResult result = WeatherResult.builder()
            .city(location.getName())
            .country(location.getCountry())
            .temperature(current.getTemperature2m())
            .temperatureUnit(tempUnit)
            .apparentTemperature(current.getApparentTemperature())
            .apparentTemperatureUnit(currentUnits.getApparentTemperature())
            .precipitation(current.getPrecipitation())
            .precipitationUnit(currentUnits.getPrecipitation())
            .rain(current.getRain())
            .rainUnit(currentUnits.getRain())
            .showers(current.getShowers())
            .showersUnit(currentUnits.getShowers())
            .snowfall(current.getSnowfall())
            .snowfallUnit(currentUnits.getSnowfall())
            .cloudCover(current.getCloudCover())
            .cloudCoverUnit(currentUnits.getCloudCover())
            .windSpeed(current.getWindSpeed10m())
            .windSpeedUnit(currentUnits.getWindSpeed10m())
            .humidity(current.getRelativeHumidity2m())
            .humidityUnit(currentUnits.getRelativeHumidity2m())
            .day(isDay)
            .weatherCode(current.getWeatherCode())
            .conditionDescription(weatherCondition.getDescription())
            .visualType(weatherCondition.getVisualType())
            .sunrise(formatTime(extractFirstString(daily != null ? daily.getSunrise() : null)))
            .sunset(formatTime(extractFirstString(daily != null ? daily.getSunset() : null)))
            .sunshineDurationHours(convertSecondsToHours(extractFirstDouble(daily != null ? daily.getSunshineDuration() : null)))
            .sunshineDurationUnit("h")
            .forecast(buildForecast(weatherResponse, tempUnit))
            .fromCache(false)
            .build();

        cache.put(normalizedCity, result);
        return result;
    }

    private List<DailyForecastEntry> buildForecast(WeatherResponse weatherResponse, String tempUnit) {
        WeatherResponse.Daily daily = weatherResponse.getDaily();
        if (daily == null) return Collections.emptyList();

        List<String> dates = daily.getTime();
        List<Double> maxTemps = daily.getTemperatureMax();
        List<Double> minTemps = daily.getTemperatureMin();
        List<Double> precips = daily.getPrecipitationSum();
        List<Integer> codes = daily.getWeatherCode();

        if (dates == null || maxTemps == null || minTemps == null || precips == null || codes == null) {
            return Collections.emptyList();
        }

        String precipUnit = "mm";
        if (weatherResponse.getDailyUnits() != null
                && weatherResponse.getDailyUnits().getPrecipitationSum() != null) {
            precipUnit = weatherResponse.getDailyUnits().getPrecipitationSum();
        }

        int size = Math.min(dates.size(),
                Math.min(maxTemps.size(),
                Math.min(minTemps.size(),
                Math.min(precips.size(), codes.size()))));

        List<DailyForecastEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int code = codes.get(i) != null ? codes.get(i) : 0;
            WeatherCondition condition = WeatherConditionInterpreter.interpret(code, true);
            entries.add(new DailyForecastEntry(
                dates.get(i),
                maxTemps.get(i) != null ? maxTemps.get(i) : 0.0,
                minTemps.get(i) != null ? minTemps.get(i) : 0.0,
                tempUnit,
                condition.getDescription(),
                condition.getVisualType(),
                precips.get(i) != null ? precips.get(i) : 0.0,
                precipUnit
            ));
        }
        return entries;
    }

    private String normalizeCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            LOGGER.warning("Citta non valida ricevuta: '" + city + "'");
            throw new IllegalArgumentException("Devi inserire una citta valida.");
        }

        return city.trim();
    }

    private String extractFirstString(List<String> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private Double extractFirstDouble(List<Double> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private String formatTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return null;
        }

        return LocalDateTime.parse(dateTime, API_DATE_TIME_FORMATTER).format(OUTPUT_TIME_FORMATTER);
    }

    private Double convertSecondsToHours(Double seconds) {
        if (seconds == null) {
            return null;
        }

        return seconds / 3600.0;
    }
}
