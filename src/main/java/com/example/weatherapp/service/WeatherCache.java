package com.example.weatherapp.service;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache a TTL fisso (1 ora) per i risultati meteo.
 *
 * <p>Thread-safe via {@link ConcurrentHashMap}; le entry scadute vengono
 * rimosse in modo lazy alla prima lettura successiva per la stessa città,
 * senza alcun thread di pulizia in background.
 *
 * <p>La chiave è il nome della città normalizzato (lowercase, trimmed)
 * così "Roma", "roma" e "  ROMA  " colpiscono la stessa entry.
 */
public final class WeatherCache {

    static final long TTL_MS = 60L * 60 * 1000;

    private final ConcurrentHashMap<String, CacheEntry> store = new ConcurrentHashMap<>();

    /**
     * Restituisce il risultato meteo in cache per la città indicata,
     * oppure {@code null} se assente o scaduto.
     *
     * @param city nome della città (non {@code null})
     * @return il {@link WeatherResult} in cache, o {@code null}
     */
    public WeatherResult get(String city) {
        String key = normalize(city);
        CacheEntry entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (System.currentTimeMillis() - entry.timestamp > TTL_MS) {
            store.remove(key, entry);
            return null;
        }
        return entry.result;
    }

    /**
     * Inserisce o aggiorna l'entry in cache per la città indicata.
     *
     * @param city   nome della città (non {@code null})
     * @param result risultato meteo da memorizzare
     */
    public void put(String city, WeatherResult result) {
        store.put(normalize(city), new CacheEntry(result, System.currentTimeMillis()));
    }

    private static String normalize(String city) {
        return city.trim().toLowerCase(Locale.ROOT);
    }

    private static final class CacheEntry {
        final WeatherResult result;
        final long timestamp;

        CacheEntry(WeatherResult result, long timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }
    }
}
