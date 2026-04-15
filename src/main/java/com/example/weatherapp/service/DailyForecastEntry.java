package com.example.weatherapp.service;

/**
 * Dati meteo previsti per un singolo giorno futuro.
 *
 * <p>Oggetto immutabile prodotto da {@link WeatherService} a partire
 * dai dati giornalieri dell'API Open-Meteo (endpoint {@code /v1/forecast},
 * sezione {@code daily}).
 */
public final class DailyForecastEntry {

    private final String date;
    private final double maxTemp;
    private final double minTemp;
    private final String tempUnit;
    private final String conditionDescription;
    private final WeatherVisualType visualType;
    private final double precipitationSum;
    private final String precipitationUnit;

    public DailyForecastEntry(
            String date,
            double maxTemp,
            double minTemp,
            String tempUnit,
            String conditionDescription,
            WeatherVisualType visualType,
            double precipitationSum,
            String precipitationUnit) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.tempUnit = tempUnit;
        this.conditionDescription = conditionDescription;
        this.visualType = visualType;
        this.precipitationSum = precipitationSum;
        this.precipitationUnit = precipitationUnit;
    }

    /** Data nel formato {@code YYYY-MM-DD}, es. {@code "2026-04-15"}. */
    public String getDate() { return date; }

    /** Temperatura massima del giorno. */
    public double getMaxTemp() { return maxTemp; }

    /** Temperatura minima del giorno. */
    public double getMinTemp() { return minTemp; }

    /** Unità di misura delle temperature (es. {@code "°C"}). */
    public String getTempUnit() { return tempUnit; }

    /** Descrizione testuale della condizione meteorologica principale. */
    public String getConditionDescription() { return conditionDescription; }

    /** Tipo visivo associato alla condizione (usato per scegliere l'icona). */
    public WeatherVisualType getVisualType() { return visualType; }

    /** Somma delle precipitazioni previste nel giorno. */
    public double getPrecipitationSum() { return precipitationSum; }

    /** Unità di misura delle precipitazioni (es. {@code "mm"}). */
    public String getPrecipitationUnit() { return precipitationUnit; }
}
