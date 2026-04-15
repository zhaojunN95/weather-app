package com.example.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherResponse {

    @JsonProperty("current_units")
    private CurrentUnits currentUnits;
    private Current current;
    @JsonProperty("daily_units")
    private DailyUnits dailyUnits;
    private Daily daily;

    public CurrentUnits getCurrentUnits() {
        return currentUnits;
    }

    public void setCurrentUnits(CurrentUnits currentUnits) {
        this.currentUnits = currentUnits;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public DailyUnits getDailyUnits() {
        return dailyUnits;
    }

    public void setDailyUnits(DailyUnits dailyUnits) {
        this.dailyUnits = dailyUnits;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public static class CurrentUnits {
        @JsonProperty("temperature_2m")
        private String temperature2m;
        @JsonProperty("apparent_temperature")
        private String apparentTemperature;
        private String precipitation;
        private String rain;
        private String showers;
        private String snowfall;
        @JsonProperty("cloud_cover")
        private String cloudCover;
        @JsonProperty("wind_speed_10m")
        private String windSpeed10m;
        @JsonProperty("relative_humidity_2m")
        private String relativeHumidity2m;

        public String getTemperature2m() { return temperature2m; }
        public void setTemperature2m(String temperature2m) { this.temperature2m = temperature2m; }
        public String getApparentTemperature() { return apparentTemperature; }
        public void setApparentTemperature(String apparentTemperature) { this.apparentTemperature = apparentTemperature; }
        public String getPrecipitation() { return precipitation; }
        public void setPrecipitation(String precipitation) { this.precipitation = precipitation; }
        public String getRain() { return rain; }
        public void setRain(String rain) { this.rain = rain; }
        public String getShowers() { return showers; }
        public void setShowers(String showers) { this.showers = showers; }
        public String getSnowfall() { return snowfall; }
        public void setSnowfall(String snowfall) { this.snowfall = snowfall; }
        public String getCloudCover() { return cloudCover; }
        public void setCloudCover(String cloudCover) { this.cloudCover = cloudCover; }
        public String getWindSpeed10m() { return windSpeed10m; }
        public void setWindSpeed10m(String windSpeed10m) { this.windSpeed10m = windSpeed10m; }
        public String getRelativeHumidity2m() { return relativeHumidity2m; }
        public void setRelativeHumidity2m(String relativeHumidity2m) { this.relativeHumidity2m = relativeHumidity2m; }
    }

    public static class Current {
        @JsonProperty("temperature_2m")
        private double temperature2m;
        @JsonProperty("apparent_temperature")
        private double apparentTemperature;
        private double precipitation;
        private double rain;
        private double showers;
        private double snowfall;
        @JsonProperty("cloud_cover")
        private double cloudCover;
        @JsonProperty("wind_speed_10m")
        private double windSpeed10m;
        @JsonProperty("is_day")
        private int isDay;
        @JsonProperty("weather_code")
        private int weatherCode;
        @JsonProperty("relative_humidity_2m")
        private int relativeHumidity2m;

        public double getTemperature2m() { return temperature2m; }
        public void setTemperature2m(double temperature2m) { this.temperature2m = temperature2m; }
        public double getApparentTemperature() { return apparentTemperature; }
        public void setApparentTemperature(double apparentTemperature) { this.apparentTemperature = apparentTemperature; }
        public double getPrecipitation() { return precipitation; }
        public void setPrecipitation(double precipitation) { this.precipitation = precipitation; }
        public double getRain() { return rain; }
        public void setRain(double rain) { this.rain = rain; }
        public double getShowers() { return showers; }
        public void setShowers(double showers) { this.showers = showers; }
        public double getSnowfall() { return snowfall; }
        public void setSnowfall(double snowfall) { this.snowfall = snowfall; }
        public double getCloudCover() { return cloudCover; }
        public void setCloudCover(double cloudCover) { this.cloudCover = cloudCover; }
        public double getWindSpeed10m() { return windSpeed10m; }
        public void setWindSpeed10m(double windSpeed10m) { this.windSpeed10m = windSpeed10m; }
        public int getIsDay() { return isDay; }
        public void setIsDay(int isDay) { this.isDay = isDay; }
        public int getWeatherCode() { return weatherCode; }
        public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }
        public int getRelativeHumidity2m() { return relativeHumidity2m; }
        public void setRelativeHumidity2m(int relativeHumidity2m) { this.relativeHumidity2m = relativeHumidity2m; }
    }

    public static class DailyUnits {
        private String sunrise;
        private String sunset;
        @JsonProperty("sunshine_duration")
        private String sunshineDuration;
        @JsonProperty("temperature_2m_max")
        private String temperatureMax;
        @JsonProperty("precipitation_sum")
        private String precipitationSum;

        public String getSunrise() { return sunrise; }
        public void setSunrise(String sunrise) { this.sunrise = sunrise; }
        public String getSunset() { return sunset; }
        public void setSunset(String sunset) { this.sunset = sunset; }
        public String getSunshineDuration() { return sunshineDuration; }
        public void setSunshineDuration(String sunshineDuration) { this.sunshineDuration = sunshineDuration; }
        public String getTemperatureMax() { return temperatureMax; }
        public void setTemperatureMax(String temperatureMax) { this.temperatureMax = temperatureMax; }
        public String getPrecipitationSum() { return precipitationSum; }
        public void setPrecipitationSum(String precipitationSum) { this.precipitationSum = precipitationSum; }
    }

    public static class Daily {
        private List<String> time;
        private List<String> sunrise;
        private List<String> sunset;
        @JsonProperty("sunshine_duration")
        private List<Double> sunshineDuration;
        @JsonProperty("temperature_2m_max")
        private List<Double> temperatureMax;
        @JsonProperty("temperature_2m_min")
        private List<Double> temperatureMin;
        @JsonProperty("precipitation_sum")
        private List<Double> precipitationSum;
        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        public List<String> getTime() { return time; }
        public void setTime(List<String> time) { this.time = time; }
        public List<String> getSunrise() { return sunrise; }
        public void setSunrise(List<String> sunrise) { this.sunrise = sunrise; }
        public List<String> getSunset() { return sunset; }
        public void setSunset(List<String> sunset) { this.sunset = sunset; }
        public List<Double> getSunshineDuration() { return sunshineDuration; }
        public void setSunshineDuration(List<Double> sunshineDuration) { this.sunshineDuration = sunshineDuration; }
        public List<Double> getTemperatureMax() { return temperatureMax; }
        public void setTemperatureMax(List<Double> temperatureMax) { this.temperatureMax = temperatureMax; }
        public List<Double> getTemperatureMin() { return temperatureMin; }
        public void setTemperatureMin(List<Double> temperatureMin) { this.temperatureMin = temperatureMin; }
        public List<Double> getPrecipitationSum() { return precipitationSum; }
        public void setPrecipitationSum(List<Double> precipitationSum) { this.precipitationSum = precipitationSum; }
        public List<Integer> getWeatherCode() { return weatherCode; }
        public void setWeatherCode(List<Integer> weatherCode) { this.weatherCode = weatherCode; }
    }
}
