package com.example.weatherapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherConditionInterpreterTest {

    // --- Codice 0: cielo sereno ---

    @Test
    void code0_day_shouldBeSolePieno() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(0, true);
        assertEquals("Sole pieno", cond.getDescription());
        assertEquals(WeatherVisualType.CLEAR, cond.getVisualType());
    }

    @Test
    void code0_night_shouldBeCieloSereno() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(0, false);
        assertEquals("Cielo sereno", cond.getDescription());
        assertEquals(WeatherVisualType.CLEAR, cond.getVisualType());
    }

    // --- Codice 1: prevalentemente sereno/soleggiato ---

    @Test
    void code1_day_shouldBePrevalentementeSoleggiato() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(1, true);
        assertEquals("Prevalentemente soleggiato", cond.getDescription());
        assertEquals(WeatherVisualType.PARTLY_CLOUDY, cond.getVisualType());
    }

    @Test
    void code1_night_shouldBePrevalentementeSereno() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(1, false);
        assertEquals("Prevalentemente sereno", cond.getDescription());
        assertEquals(WeatherVisualType.PARTLY_CLOUDY, cond.getVisualType());
    }

    // --- Codice 2: parzialmente nuvoloso ---

    @Test
    void code2_shouldBeParzialmenteNuvoloso() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(2, true);
        assertEquals("Parzialmente nuvoloso", cond.getDescription());
        assertEquals(WeatherVisualType.PARTLY_CLOUDY, cond.getVisualType());
    }

    // --- Codice 3: coperto ---

    @Test
    void code3_shouldBeCoperto() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(3, true);
        assertEquals("Coperto", cond.getDescription());
        assertEquals(WeatherVisualType.CLOUDY, cond.getVisualType());
    }

    // --- Codici 45 e 48: nebbia ---

    @ParameterizedTest
    @ValueSource(ints = {45, 48})
    void fogCodes_shouldBeNebbia(int code) {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(code, true);
        assertEquals("Nebbia", cond.getDescription());
        assertEquals(WeatherVisualType.FOG, cond.getVisualType());
    }

    // --- Codici 51-57: pioviggine ---

    @ParameterizedTest
    @ValueSource(ints = {51, 53, 55, 56, 57})
    void drizzleCodes_shouldBePioviggine(int code) {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(code, true);
        assertEquals("Pioviggine", cond.getDescription());
        assertEquals(WeatherVisualType.DRIZZLE, cond.getVisualType());
    }

    // --- Codici pioggia: 61-67 e 80-82 ---

    @ParameterizedTest
    @ValueSource(ints = {61, 63, 65, 66, 67, 80, 81, 82})
    void rainCodes_shouldBePioggia(int code) {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(code, true);
        assertEquals("Pioggia", cond.getDescription());
        assertEquals(WeatherVisualType.RAIN, cond.getVisualType());
    }

    // --- Codici neve: 71-77 e 85-86 ---

    @ParameterizedTest
    @ValueSource(ints = {71, 73, 75, 77, 85, 86})
    void snowCodes_shouldBeNeve(int code) {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(code, true);
        assertEquals("Neve", cond.getDescription());
        assertEquals(WeatherVisualType.SNOW, cond.getVisualType());
    }

    // --- Codici temporale: 95, 96, 99 ---

    @ParameterizedTest
    @ValueSource(ints = {95, 96, 99})
    void thunderstormCodes_shouldBeTemporale(int code) {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(code, true);
        assertEquals("Temporale", cond.getDescription());
        assertEquals(WeatherVisualType.THUNDERSTORM, cond.getVisualType());
    }

    // --- Codice sconosciuto: default ---

    @Test
    void unknownCode_shouldBeCondizioniVariabili() {
        WeatherCondition cond = WeatherConditionInterpreter.interpret(999, true);
        assertEquals("Condizioni variabili", cond.getDescription());
        assertEquals(WeatherVisualType.CLOUDY, cond.getVisualType());
    }
}
