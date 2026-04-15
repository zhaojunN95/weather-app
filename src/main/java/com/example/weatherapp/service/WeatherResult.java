package com.example.weatherapp.service;

import java.util.Collections;
import java.util.List;

public class WeatherResult {

    private final String city;
    private final String country;
    private final double temperature;
    private final String temperatureUnit;
    private final double apparentTemperature;
    private final String apparentTemperatureUnit;
    private final double precipitation;
    private final String precipitationUnit;
    private final double rain;
    private final String rainUnit;
    private final double showers;
    private final String showersUnit;
    private final double snowfall;
    private final String snowfallUnit;
    private final double cloudCover;
    private final String cloudCoverUnit;
    private final double windSpeed;
    private final String windSpeedUnit;
    private final int humidity;
    private final String humidityUnit;
    private final boolean day;
    private final int weatherCode;
    private final String conditionDescription;
    private final WeatherVisualType visualType;
    private final String sunrise;
    private final String sunset;
    private final Double sunshineDurationHours;
    private final String sunshineDurationUnit;
    private final List<DailyForecastEntry> forecast;
    private final boolean fromCache;

    private WeatherResult(Builder builder) {
        this.city = builder.city;
        this.country = builder.country;
        this.temperature = builder.temperature;
        this.temperatureUnit = builder.temperatureUnit;
        this.apparentTemperature = builder.apparentTemperature;
        this.apparentTemperatureUnit = builder.apparentTemperatureUnit;
        this.precipitation = builder.precipitation;
        this.precipitationUnit = builder.precipitationUnit;
        this.rain = builder.rain;
        this.rainUnit = builder.rainUnit;
        this.showers = builder.showers;
        this.showersUnit = builder.showersUnit;
        this.snowfall = builder.snowfall;
        this.snowfallUnit = builder.snowfallUnit;
        this.cloudCover = builder.cloudCover;
        this.cloudCoverUnit = builder.cloudCoverUnit;
        this.windSpeed = builder.windSpeed;
        this.windSpeedUnit = builder.windSpeedUnit;
        this.humidity = builder.humidity;
        this.humidityUnit = builder.humidityUnit;
        this.day = builder.day;
        this.weatherCode = builder.weatherCode;
        this.conditionDescription = builder.conditionDescription;
        this.visualType = builder.visualType;
        this.sunrise = builder.sunrise;
        this.sunset = builder.sunset;
        this.sunshineDurationHours = builder.sunshineDurationHours;
        this.sunshineDurationUnit = builder.sunshineDurationUnit;
        this.forecast = builder.forecast != null
                ? Collections.unmodifiableList(builder.forecast)
                : Collections.<DailyForecastEntry>emptyList();
        this.fromCache = builder.fromCache;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String city;
        private String country;
        private double temperature;
        private String temperatureUnit;
        private double apparentTemperature;
        private String apparentTemperatureUnit;
        private double precipitation;
        private String precipitationUnit;
        private double rain;
        private String rainUnit;
        private double showers;
        private String showersUnit;
        private double snowfall;
        private String snowfallUnit;
        private double cloudCover;
        private String cloudCoverUnit;
        private double windSpeed;
        private String windSpeedUnit;
        private int humidity;
        private String humidityUnit;
        private boolean day;
        private int weatherCode;
        private String conditionDescription;
        private WeatherVisualType visualType;
        private String sunrise;
        private String sunset;
        private Double sunshineDurationHours;
        private String sunshineDurationUnit;
        private List<DailyForecastEntry> forecast;
        private boolean fromCache;

        private Builder() {}

        public Builder city(String city) { this.city = city; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder temperature(double temperature) { this.temperature = temperature; return this; }
        public Builder temperatureUnit(String temperatureUnit) { this.temperatureUnit = temperatureUnit; return this; }
        public Builder apparentTemperature(double apparentTemperature) { this.apparentTemperature = apparentTemperature; return this; }
        public Builder apparentTemperatureUnit(String unit) { this.apparentTemperatureUnit = unit; return this; }
        public Builder precipitation(double precipitation) { this.precipitation = precipitation; return this; }
        public Builder precipitationUnit(String precipitationUnit) { this.precipitationUnit = precipitationUnit; return this; }
        public Builder rain(double rain) { this.rain = rain; return this; }
        public Builder rainUnit(String rainUnit) { this.rainUnit = rainUnit; return this; }
        public Builder showers(double showers) { this.showers = showers; return this; }
        public Builder showersUnit(String showersUnit) { this.showersUnit = showersUnit; return this; }
        public Builder snowfall(double snowfall) { this.snowfall = snowfall; return this; }
        public Builder snowfallUnit(String snowfallUnit) { this.snowfallUnit = snowfallUnit; return this; }
        public Builder cloudCover(double cloudCover) { this.cloudCover = cloudCover; return this; }
        public Builder cloudCoverUnit(String cloudCoverUnit) { this.cloudCoverUnit = cloudCoverUnit; return this; }
        public Builder windSpeed(double windSpeed) { this.windSpeed = windSpeed; return this; }
        public Builder windSpeedUnit(String windSpeedUnit) { this.windSpeedUnit = windSpeedUnit; return this; }
        public Builder humidity(int humidity) { this.humidity = humidity; return this; }
        public Builder humidityUnit(String humidityUnit) { this.humidityUnit = humidityUnit; return this; }
        public Builder day(boolean day) { this.day = day; return this; }
        public Builder weatherCode(int weatherCode) { this.weatherCode = weatherCode; return this; }
        public Builder conditionDescription(String conditionDescription) { this.conditionDescription = conditionDescription; return this; }
        public Builder visualType(WeatherVisualType visualType) { this.visualType = visualType; return this; }
        public Builder sunrise(String sunrise) { this.sunrise = sunrise; return this; }
        public Builder sunset(String sunset) { this.sunset = sunset; return this; }
        public Builder sunshineDurationHours(Double sunshineDurationHours) { this.sunshineDurationHours = sunshineDurationHours; return this; }
        public Builder sunshineDurationUnit(String sunshineDurationUnit) { this.sunshineDurationUnit = sunshineDurationUnit; return this; }
        public Builder forecast(List<DailyForecastEntry> forecast) { this.forecast = forecast; return this; }
        public Builder fromCache(boolean fromCache) { this.fromCache = fromCache; return this; }

        public WeatherResult build() {
            return new WeatherResult(this);
        }
    }

    public String getCity() { return city; }
    public String getCountry() { return country; }
    public double getTemperature() { return temperature; }
    public String getTemperatureUnit() { return temperatureUnit; }
    public double getApparentTemperature() { return apparentTemperature; }
    public String getApparentTemperatureUnit() { return apparentTemperatureUnit; }
    public double getPrecipitation() { return precipitation; }
    public String getPrecipitationUnit() { return precipitationUnit; }
    public double getRain() { return rain; }
    public String getRainUnit() { return rainUnit; }
    public double getShowers() { return showers; }
    public String getShowersUnit() { return showersUnit; }
    public double getSnowfall() { return snowfall; }
    public String getSnowfallUnit() { return snowfallUnit; }
    public double getCloudCover() { return cloudCover; }
    public String getCloudCoverUnit() { return cloudCoverUnit; }
    public double getWindSpeed() { return windSpeed; }
    public String getWindSpeedUnit() { return windSpeedUnit; }
    public int getHumidity() { return humidity; }
    public String getHumidityUnit() { return humidityUnit; }
    public boolean isDay() { return day; }
    public int getWeatherCode() { return weatherCode; }
    public String getConditionDescription() { return conditionDescription; }
    public WeatherVisualType getVisualType() { return visualType; }
    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
    public Double getSunshineDurationHours() { return sunshineDurationHours; }
    public String getSunshineDurationUnit() { return sunshineDurationUnit; }
    public List<DailyForecastEntry> getForecast() { return forecast; }
    public boolean isFromCache() { return fromCache; }
}

