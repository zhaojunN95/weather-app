# Weather App

App meteo in Java (Swing) che si appoggia alle API gratuite di [Open-Meteo](https://open-meteo.com/).

## Funzionalita

- **Citta singola** — inserisci il nome di una citta e ottieni le condizioni meteo correnti
- **Multi-citta** — inserisci piu citta separate da `;` (es. `Roma;Milano;Napoli`) per un confronto affiancato
- **Previsioni a 5 giorni** — card giornaliere con temperatura max/min e precipitazioni
- **Dati meteo completi** — temperatura, temperatura percepita, precipitazioni, vento, umidita, copertura nuvolosa, alba/tramonto
- **Cache 1 ora** — i risultati vengono memorizzati per 60 minuti; le richieste successive alla stessa citta restituiscono i dati dalla cache (indicatore visivo `[cache]`)
- **Icone dinamiche** — icone Swing generate a runtime in base alle condizioni meteo
- **Tema chiaro/scuro** — commutabile direttamente dalla GUI
- **Modalita console** — utilizzabile anche senza interfaccia grafica

## Requisiti

- Java 8+
- Maven 3.8+
- Connessione internet (API Open-Meteo — gratuita, nessuna API key necessaria)

## Esecuzione

### Interfaccia grafica

```bash
mvn exec:java -Dexec.mainClass="com.example.weatherapp.App"
```

Si apre una finestra Swing. Inserisci una o piu citta nel campo di testo e premi **Cerca**.

> **Multi-citta**: separa i nomi con `;`, ad esempio `Roma;Parigi;Berlino`

### Modalita console

```bash
mvn exec:java -Dexec.mainClass="com.example.weatherapp.App" -Dexec.args="--cli"
```

Quando richiesto, inserisci il nome di una citta (es. `Roma` o `Milano`).

## Test

```bash
mvn test
```

## Struttura del progetto

```
src/main/java/com/example/weatherapp/
├── App.java                          # Entry point
├── client/
│   └── OpenMeteoClient.java          # Chiamate HTTP alle API Open-Meteo
├── model/
│   ├── GeocodingResponse.java        # Modello risposta geocoding
│   └── WeatherResponse.java          # Modello risposta meteo (corrente + daily)
├── service/
│   ├── WeatherCache.java             # Cache TTL 1 ora (ConcurrentHashMap)
│   ├── WeatherResult.java            # POJO risultato meteo con builder
│   ├── WeatherResultFormatter.java   # Formattazione testo per console
│   ├── WeatherService.java           # Orchestrazione: geocoding → cache → API
│   └── DailyForecastEntry.java       # POJO per singolo giorno di previsione
└── ui/
    ├── WeatherAppFrame.java          # Finestra principale Swing (CardLayout)
    └── WeatherIconFactory.java       # Icone meteo disegnate via Graphics2D
```

## Note tecniche

- **Nessuna API key** richiesta — Open-Meteo e completamente gratuita
- **Cache**: chiave normalizzata (`trim + lowercase`); eviction pigra al momento della lettura
- **Separatore multi-citta**: `;` scelto per supportare nomi come `Washington, DC`
- **Previsioni**: parsing difensivo con `Math.min()` sulle liste daily per evitare `IndexOutOfBoundsException`
