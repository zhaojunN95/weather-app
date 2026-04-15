package com.example.weatherapp.ui;

import com.example.weatherapp.service.DailyForecastEntry;
import com.example.weatherapp.service.WeatherResult;
import com.example.weatherapp.service.WeatherService;
import com.example.weatherapp.service.WeatherVisualType;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WeatherAppFrame extends JFrame {

    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(WeatherAppFrame.class.getName());

    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color CARD_BORDER_COLOR = new Color(210, 223, 242);
    private static final Color CARD_PRIMARY_TEXT = new Color(38, 56, 89);
    private static final Color CARD_SECONDARY_TEXT = new Color(96, 114, 145);
    private static final Color ERROR_COLOR = new Color(184, 55, 55);
    private static final WeatherTheme DEFAULT_THEME = new WeatherTheme(
        new Color(235, 244, 255),
        new Color(110, 188, 255),
        new Color(255, 214, 130),
        new Color(42, 61, 95),
        new Color(96, 114, 145),
        Color.WHITE,
        new Color(239, 246, 255),
        new Color(55, 120, 246),
        Color.WHITE,
        new Color(170, 200, 239)
    );

    private static final String CARD_SINGLE = "SINGLE";
    private static final String CARD_MULTI = "MULTI";

    private final WeatherService weatherService;
    private final JPanel rootPanel;
    private final WeatherGradientPanel heroPanel;
    private final JTextField cityField;
    private final JButton searchButton;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel locationLabel;
    private final JLabel conditionLabel;
    private final JLabel temperatureLabel;
    private final JLabel temperatureIconLabel;
    private final JLabel apparentTemperatureValueLabel;
    private final JLabel humidityValueLabel;
    private final JLabel rainValueLabel;
    private final JLabel precipitationValueLabel;
    private final JLabel cloudCoverValueLabel;
    private final JLabel windValueLabel;
    private final JLabel showersValueLabel;
    private final JLabel snowfallValueLabel;
    private final JLabel sunValueLabel;
    private final JLabel statusLabel;
    private final JPanel forecastContainer;
    private final JPanel mainCardPanel;
    private final JPanel multiCityPanel;
    private final ExecutorService comparisonExecutor;
    private JLabel forecastTitleLabel;
    private Color statusDefaultColor;

    public WeatherAppFrame(WeatherService weatherService) {
        super("Weather App");
        this.weatherService = weatherService;
        this.rootPanel = new JPanel(new BorderLayout(16, 16));
        this.heroPanel = new WeatherGradientPanel(DEFAULT_THEME.heroStart, DEFAULT_THEME.heroEnd);
        this.cityField = new JTextField(22);
        this.searchButton = new JButton("Aggiorna");
        this.titleLabel = new JLabel("Weather App");
        this.subtitleLabel = new JLabel("Inserisci una città o più città separate da  ;");
        this.locationLabel = new JLabel("Inserisci una citta");
        this.conditionLabel = new JLabel("Condizioni non ancora caricate");
        this.temperatureLabel = new JLabel("--");
        this.temperatureIconLabel = new JLabel();
        this.apparentTemperatureValueLabel = createValueLabel("--");
        this.humidityValueLabel = createValueLabel("--");
        this.rainValueLabel = createValueLabel("--");
        this.precipitationValueLabel = createValueLabel("--");
        this.cloudCoverValueLabel = createValueLabel("--");
        this.windValueLabel = createValueLabel("--");
        this.showersValueLabel = createValueLabel("--");
        this.snowfallValueLabel = createValueLabel("--");
        this.sunValueLabel = createCompactValueLabel("--");
        this.statusLabel = new JLabel("Cerca una città (o più città separate da  ;  ) per vedere temperatura, umidità e previsioni.");
        this.forecastContainer = new JPanel();
        this.forecastContainer.setOpaque(false);
        this.mainCardPanel = new JPanel(new CardLayout());
        this.mainCardPanel.setOpaque(false);
        this.multiCityPanel = new JPanel();
        this.multiCityPanel.setOpaque(false);
        this.comparisonExecutor = Executors.newFixedThreadPool(5);
        this.statusDefaultColor = DEFAULT_THEME.headerSecondaryText;

        configureFrame();
        bindActions();
        applyTheme(DEFAULT_THEME);
    }

    private void configureFrame() {
        rootPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        setContentPane(rootPanel);
        setIconImage(WeatherIconFactory.createAppIconImage(64));
        rootPanel.add(createTopPanel(), BorderLayout.NORTH);
        rootPanel.add(createMainCardPanel(), BorderLayout.CENTER);
        rootPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        getRootPane().setDefaultButton(searchButton);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void dispose() {
        comparisonExecutor.shutdownNow();
        super.dispose();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setOpaque(false);

        JPanel brandingPanel = new JPanel();
        brandingPanel.setOpaque(false);
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.X_AXIS));

        JLabel logoLabel = new JLabel(WeatherIconFactory.createAppIcon(44));
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        brandingPanel.add(logoLabel);
        brandingPanel.add(Box.createHorizontalStrut(12));
        brandingPanel.add(titlePanel);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        cityField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        searchPanel.add(cityField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(brandingPanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMainCardPanel() {
        mainCardPanel.add(createSingleCityPanel(), CARD_SINGLE);
        JScrollPane multiScroll = new JScrollPane(multiCityPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        multiScroll.setOpaque(false);
        multiScroll.getViewport().setOpaque(false);
        multiScroll.setBorder(null);
        mainCardPanel.add(multiScroll, CARD_MULTI);
        return mainCardPanel;
    }

    private JPanel createSingleCityPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.add(createHeroPanel(), BorderLayout.NORTH);

        JPanel detailsAndForecast = new JPanel();
        detailsAndForecast.setLayout(new BoxLayout(detailsAndForecast, BoxLayout.Y_AXIS));
        detailsAndForecast.setOpaque(false);

        JPanel detailsWrapper = createDetailsPanel();
        detailsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsAndForecast.add(detailsWrapper);
        detailsAndForecast.add(Box.createVerticalStrut(16));

        JPanel forecastSection = new JPanel(new BorderLayout(0, 8));
        forecastSection.setOpaque(false);
        forecastSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        forecastTitleLabel = new JLabel("Previsioni 5 giorni");
        forecastTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        forecastSection.add(forecastTitleLabel, BorderLayout.NORTH);
        forecastSection.add(forecastContainer, BorderLayout.CENTER);
        detailsAndForecast.add(forecastSection);

        panel.add(detailsAndForecast, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCenterPanel() {
        return createSingleCityPanel();
    }

    private JPanel createHeroPanel() {
        heroPanel.setLayout(new BorderLayout(18, 0));
        heroPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        temperatureIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        temperatureIconLabel.setPreferredSize(new Dimension(150, 150));
        temperatureIconLabel.setIcon(WeatherIconFactory.createConditionIcon(null, true, 126));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        locationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        conditionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        temperatureLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));

        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        temperatureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(locationLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(temperatureLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(conditionLabel);

        heroPanel.add(temperatureIconLabel, BorderLayout.WEST);
        heroPanel.add(textPanel, BorderLayout.CENTER);
        return heroPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel rowsPanel = new JPanel();
        rowsPanel.setOpaque(false);
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));

        JPanel firstRow = new JPanel(new java.awt.GridLayout(1, 5, 10, 10));
        firstRow.setOpaque(false);
        firstRow.add(createDetailCard("Percepita", "temperature", apparentTemperatureValueLabel));
        firstRow.add(createDetailCard("Umidità", "humidity", humidityValueLabel));
        firstRow.add(createDetailCard("Pioggia", "rain", rainValueLabel));
        firstRow.add(createDetailCard("Precipitazioni", "precipitation", precipitationValueLabel));
        firstRow.add(createDetailCard("Nuvolosità", "cloud", cloudCoverValueLabel));

        JPanel secondRow = new JPanel(new java.awt.GridLayout(1, 4, 10, 10));
        secondRow.setOpaque(false);
        secondRow.add(createDetailCard("Vento", "wind", windValueLabel));
        secondRow.add(createDetailCard("Rovesci", "showers", showersValueLabel));
        secondRow.add(createDetailCard("Neve", "snow", snowfallValueLabel));
        secondRow.add(createDetailCard("Sole", "sun", sunValueLabel));

        firstRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        secondRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowsPanel.add(firstRow);
        rowsPanel.add(Box.createVerticalStrut(10));
        rowsPanel.add(secondRow);
        return rowsPanel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailCard(String title, String iconKey, JLabel valueLabel) {
        JPanel card = createCardPanel(new BorderLayout(0, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        JLabel iconLabel = new JLabel(WeatherIconFactory.createMetricIcon(iconKey, 18));
        JLabel titleTextLabel = new JLabel(title);
        titleTextLabel.setForeground(CARD_SECONDARY_TEXT);
        titleTextLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(8));
        headerPanel.add(titleTextLabel);
        headerPanel.add(Box.createHorizontalGlue());

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JLabel createValueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(CARD_PRIMARY_TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        return label;
    }

    private JLabel createCompactValueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(CARD_PRIMARY_TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }

    private void bindActions() {
        searchButton.addActionListener(event -> loadWeather());
        cityField.addActionListener(event -> loadWeather());
    }

    private void loadWeather() {
        final String input = cityField.getText();

        if (input == null || input.trim().isEmpty()) {
            showError("Devi inserire almeno una città valida.");
            return;
        }

        List<String> cities = new ArrayList<>();
        for (String part : input.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                cities.add(trimmed);
            }
        }

        if (cities.isEmpty()) {
            showError("Devi inserire almeno una città valida.");
            return;
        }

        if (cities.size() > 5) {
            cities = cities.subList(0, 5);
        }

        setLoading(true);
        statusLabel.setForeground(statusDefaultColor);
        statusLabel.setText("Recupero dati meteo in corso...");

        if (cities.size() == 1) {
            loadSingleCity(cities.get(0));
        } else {
            loadMultipleCities(cities);
        }
    }

    private void loadSingleCity(final String city) {
        new SwingWorker<WeatherResult, Void>() {
            @Override
            protected WeatherResult doInBackground() throws Exception {
                return weatherService.getWeather(city);
            }

            @Override
            protected void done() {
                try {
                    WeatherResult result = get();
                    ((CardLayout) mainCardPanel.getLayout()).show(mainCardPanel, CARD_SINGLE);
                    renderWeather(result);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Operazione interrotta.");
                } catch (ExecutionException ex) {
                    handleWeatherError(ex.getCause());
                } finally {
                    setLoading(false);
                    cityField.requestFocusInWindow();
                    cityField.selectAll();
                }
            }
        }.execute();
    }

    private void loadMultipleCities(final List<String> cities) {
        new SwingWorker<List<WeatherResult>, Void>() {
            @Override
            protected List<WeatherResult> doInBackground() throws Exception {
                List<Callable<WeatherResult>> tasks = new ArrayList<>();
                for (final String city : cities) {
                    tasks.add(new Callable<WeatherResult>() {
                        @Override
                        public WeatherResult call() throws Exception {
                            return weatherService.getWeather(city);
                        }
                    });
                }
                List<Future<WeatherResult>> futures =
                    comparisonExecutor.invokeAll(tasks, 30, TimeUnit.SECONDS);
                List<WeatherResult> results = new ArrayList<>();
                for (Future<WeatherResult> f : futures) {
                    try {
                        results.add(f.get());
                    } catch (ExecutionException ex) {
                        LOGGER.warning("Citta non caricata: " + ex.getCause().getMessage());
                    }
                }
                if (results.isEmpty()) {
                    throw new IllegalArgumentException("Nessuna città trovata.");
                }
                return results;
            }

            @Override
            protected void done() {
                try {
                    List<WeatherResult> results = get();
                    ((CardLayout) mainCardPanel.getLayout()).show(mainCardPanel, CARD_MULTI);
                    renderMultiCity(results);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Operazione interrotta.");
                } catch (ExecutionException ex) {
                    handleWeatherError(ex.getCause());
                } finally {
                    setLoading(false);
                    cityField.requestFocusInWindow();
                    cityField.selectAll();
                }
            }
        }.execute();
    }

    private void handleWeatherError(Throwable cause) {
        if (cause instanceof IOException) {
            showError("Errore di rete durante il recupero dei dati meteo: " + cause.getMessage());
        } else if (cause instanceof IllegalArgumentException || cause instanceof IllegalStateException) {
            showError(cause.getMessage());
        } else {
            showError("Errore inatteso: " + cause.getMessage());
        }
    }

    private void setLoading(boolean loading) {
        cityField.setEnabled(!loading);
        searchButton.setEnabled(!loading);
    }

    private void renderWeather(WeatherResult weatherResult) {
        applyTheme(resolveTheme(weatherResult));

        locationLabel.setText(buildLocation(weatherResult));
        conditionLabel.setText((weatherResult.isDay() ? "Giorno" : "Notte") + " • " + weatherResult.getConditionDescription());
        temperatureLabel.setText(formatValue(weatherResult.getTemperature(), weatherResult.getTemperatureUnit()));
        temperatureIconLabel.setIcon(WeatherIconFactory.createConditionIcon(
            weatherResult.getVisualType(),
            weatherResult.isDay(),
            126
        ));

        apparentTemperatureValueLabel.setText(formatValue(weatherResult.getApparentTemperature(), weatherResult.getApparentTemperatureUnit()));
        humidityValueLabel.setText(weatherResult.getHumidity() + " " + (weatherResult.getHumidityUnit() != null ? weatherResult.getHumidityUnit() : "%"));
        rainValueLabel.setText(formatValue(weatherResult.getRain(), weatherResult.getRainUnit()));
        precipitationValueLabel.setText(formatValue(weatherResult.getPrecipitation(), weatherResult.getPrecipitationUnit()));
        cloudCoverValueLabel.setText(formatValue(weatherResult.getCloudCover(), weatherResult.getCloudCoverUnit()));
        windValueLabel.setText(formatValue(weatherResult.getWindSpeed(), weatherResult.getWindSpeedUnit()));
        showersValueLabel.setText(formatValue(weatherResult.getShowers(), weatherResult.getShowersUnit()));
        snowfallValueLabel.setText(formatValue(weatherResult.getSnowfall(), weatherResult.getSnowfallUnit()));
        sunValueLabel.setText(toHtml(buildSunSummary(weatherResult)));

        renderForecast(weatherResult.getForecast());

        String cacheNote = weatherResult.isFromCache() ? " (da cache)" : "";
        statusLabel.setForeground(statusDefaultColor);
        statusLabel.setText("Dati aggiornati con successo per " + weatherResult.getCity() + "." + cacheNote);
    }

    private void renderForecast(List<DailyForecastEntry> forecast) {
        forecastContainer.removeAll();
        if (!forecast.isEmpty()) {
            forecastContainer.setLayout(new java.awt.GridLayout(1, forecast.size(), 10, 0));
            for (DailyForecastEntry entry : forecast) {
                forecastContainer.add(createForecastDayCard(entry));
            }
        }
        forecastContainer.revalidate();
        forecastContainer.repaint();
    }

    private JPanel createForecastDayCard(DailyForecastEntry entry) {
        JPanel card = createCardPanel(new BorderLayout(0, 6));

        JLabel dateLabel = new JLabel(formatForecastDate(entry.getDate()), SwingConstants.CENTER);
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        dateLabel.setForeground(CARD_SECONDARY_TEXT);

        JLabel iconLabel = new JLabel(WeatherIconFactory.createConditionIcon(entry.getVisualType(), true, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel tempLabel = new JLabel(
            String.format(Locale.ITALY, "%.0f° / %.0f° %s", entry.getMaxTemp(), entry.getMinTemp(), entry.getTempUnit()),
            SwingConstants.CENTER);
        tempLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tempLabel.setForeground(CARD_PRIMARY_TEXT);

        JLabel condLabel = new JLabel(entry.getConditionDescription(), SwingConstants.CENTER);
        condLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        condLabel.setForeground(CARD_SECONDARY_TEXT);

        JLabel precipLabel = new JLabel(
            String.format(Locale.ITALY, "%.1f %s", entry.getPrecipitationSum(), entry.getPrecipitationUnit()),
            SwingConstants.CENTER);
        precipLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        precipLabel.setForeground(CARD_SECONDARY_TEXT);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        condLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        precipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(iconLabel);
        center.add(Box.createVerticalStrut(4));
        center.add(tempLabel);
        center.add(Box.createVerticalStrut(2));
        center.add(condLabel);
        center.add(Box.createVerticalStrut(2));
        center.add(precipLabel);

        card.add(dateLabel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private String formatForecastDate(String date) {
        if (date == null || date.isEmpty()) return "--";
        try {
            LocalDate localDate = LocalDate.parse(date);
            String dayName = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALY);
            return dayName + " " + localDate.getDayOfMonth() + "/" + localDate.getMonthValue();
        } catch (Exception ex) {
            return date;
        }
    }

    private void renderMultiCity(List<WeatherResult> results) {
        multiCityPanel.removeAll();
        multiCityPanel.setLayout(new java.awt.GridLayout(1, results.size(), 16, 0));
        for (WeatherResult result : results) {
            multiCityPanel.add(createCityComparisonCard(result));
        }
        multiCityPanel.revalidate();
        multiCityPanel.repaint();

        StringBuilder cities = new StringBuilder();
        for (WeatherResult r : results) {
            if (cities.length() > 0) cities.append(", ");
            cities.append(r.getCity());
        }
        statusLabel.setForeground(statusDefaultColor);
        statusLabel.setText("Confronto aggiornato: " + cities);
    }

    private JPanel createCityComparisonCard(WeatherResult result) {
        JPanel card = createCardPanel(new BorderLayout(0, 12));

        JLabel iconLabel = new JLabel(WeatherIconFactory.createConditionIcon(result.getVisualType(), result.isDay(), 64));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel cityLabel = new JLabel(buildLocation(result), SwingConstants.CENTER);
        cityLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        cityLabel.setForeground(CARD_PRIMARY_TEXT);

        JLabel tempLabel = new JLabel(formatValue(result.getTemperature(), result.getTemperatureUnit()), SwingConstants.CENTER);
        tempLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        tempLabel.setForeground(CARD_PRIMARY_TEXT);

        JLabel condLabel = new JLabel((result.isDay() ? "Giorno" : "Notte") + " • " + result.getConditionDescription(), SwingConstants.CENTER);
        condLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        condLabel.setForeground(CARD_SECONDARY_TEXT);

        JPanel heroPanel = new JPanel();
        heroPanel.setOpaque(false);
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        condLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        heroPanel.add(iconLabel);
        heroPanel.add(Box.createVerticalStrut(8));
        heroPanel.add(cityLabel);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(tempLabel);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(condLabel);

        JPanel statsPanel = new JPanel(new java.awt.GridLayout(3, 2, 6, 4));
        statsPanel.setOpaque(false);
        addStatRow(statsPanel, "Umidità",
            result.getHumidity() + " " + (result.getHumidityUnit() != null ? result.getHumidityUnit() : "%"));
        addStatRow(statsPanel, "Vento", formatValue(result.getWindSpeed(), result.getWindSpeedUnit()));
        addStatRow(statsPanel, "Pioggia", formatValue(result.getRain(), result.getRainUnit()));

        card.add(heroPanel, BorderLayout.CENTER);
        card.add(statsPanel, BorderLayout.SOUTH);
        return card;
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        labelComp.setForeground(CARD_SECONDARY_TEXT);
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        valueComp.setForeground(CARD_PRIMARY_TEXT);
        panel.add(labelComp);
        panel.add(valueComp);
    }

    private void applyTheme(WeatherTheme theme) {
        rootPanel.setBackground(theme.pageBackground);
        heroPanel.setGradientColors(theme.heroStart, theme.heroEnd);

        titleLabel.setForeground(theme.headerPrimaryText);
        subtitleLabel.setForeground(theme.headerSecondaryText);
        locationLabel.setForeground(theme.heroPrimaryText);
        temperatureLabel.setForeground(theme.heroPrimaryText);
        conditionLabel.setForeground(theme.heroSecondaryText);

        searchButton.setBackground(theme.buttonBackground);
        searchButton.setForeground(theme.buttonForeground);
        cityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(theme.fieldBorderColor),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        if (forecastTitleLabel != null) {
            forecastTitleLabel.setForeground(theme.headerPrimaryText);
        }

        statusDefaultColor = theme.headerSecondaryText;
        repaint();
    }

    private WeatherTheme resolveTheme(WeatherResult weatherResult) {
        WeatherVisualType visualType = weatherResult.getVisualType();

        if (!weatherResult.isDay()) {
            switch (visualType) {
                case CLEAR:
                    return new WeatherTheme(
                        new Color(27, 36, 63),
                        new Color(44, 61, 120),
                        new Color(90, 95, 188),
                        new Color(236, 241, 255),
                        new Color(194, 205, 235),
                        Color.WHITE,
                        new Color(220, 228, 255),
                        new Color(114, 132, 255),
                        Color.WHITE,
                        new Color(94, 116, 192)
                    );
                case THUNDERSTORM:
                    return new WeatherTheme(
                        new Color(32, 28, 58),
                        new Color(59, 46, 119),
                        new Color(26, 74, 124),
                        new Color(239, 239, 255),
                        new Color(202, 205, 233),
                        Color.WHITE,
                        new Color(224, 231, 255),
                        new Color(120, 113, 255),
                        Color.WHITE,
                        new Color(106, 102, 191)
                    );
                case RAIN:
                case DRIZZLE:
                case FOG:
                case CLOUDY:
                case PARTLY_CLOUDY:
                case SNOW:
                default:
                    return new WeatherTheme(
                        new Color(39, 48, 78),
                        new Color(72, 93, 148),
                        new Color(112, 134, 191),
                        new Color(235, 241, 255),
                        new Color(191, 205, 236),
                        Color.WHITE,
                        new Color(220, 229, 255),
                        new Color(104, 148, 255),
                        Color.WHITE,
                        new Color(92, 118, 170)
                    );
            }
        }

        switch (visualType) {
            case CLEAR:
                return new WeatherTheme(
                    new Color(235, 244, 255),
                    new Color(102, 188, 255),
                    new Color(255, 214, 130),
                    new Color(42, 61, 95),
                    new Color(96, 114, 145),
                    Color.WHITE,
                    new Color(239, 246, 255),
                    new Color(55, 120, 246),
                    Color.WHITE,
                    new Color(170, 200, 239)
                );
            case PARTLY_CLOUDY:
                return new WeatherTheme(
                    new Color(236, 244, 252),
                    new Color(122, 176, 231),
                    new Color(242, 196, 153),
                    new Color(47, 71, 102),
                    new Color(104, 124, 146),
                    Color.WHITE,
                    new Color(240, 246, 255),
                    new Color(72, 133, 246),
                    Color.WHITE,
                    new Color(168, 196, 226)
                );
            case CLOUDY:
                return new WeatherTheme(
                    new Color(237, 241, 248),
                    new Color(140, 160, 193),
                    new Color(189, 201, 221),
                    new Color(49, 65, 92),
                    new Color(107, 121, 144),
                    Color.WHITE,
                    new Color(238, 243, 252),
                    new Color(92, 122, 174),
                    Color.WHITE,
                    new Color(170, 184, 208)
                );
            case RAIN:
            case DRIZZLE:
                return new WeatherTheme(
                    new Color(229, 238, 247),
                    new Color(95, 127, 181),
                    new Color(133, 168, 212),
                    new Color(43, 63, 95),
                    new Color(93, 111, 143),
                    Color.WHITE,
                    new Color(227, 238, 255),
                    new Color(70, 123, 214),
                    Color.WHITE,
                    new Color(153, 181, 217)
                );
            case SNOW:
                return new WeatherTheme(
                    new Color(242, 248, 255),
                    new Color(185, 212, 238),
                    new Color(228, 240, 252),
                    new Color(58, 80, 114),
                    new Color(112, 132, 162),
                    new Color(45, 74, 112),
                    new Color(82, 106, 138),
                    new Color(91, 145, 224),
                    Color.WHITE,
                    new Color(180, 210, 239)
                );
            case THUNDERSTORM:
                return new WeatherTheme(
                    new Color(233, 234, 248),
                    new Color(95, 82, 162),
                    new Color(71, 108, 184),
                    new Color(56, 55, 101),
                    new Color(103, 102, 148),
                    Color.WHITE,
                    new Color(233, 237, 255),
                    new Color(104, 98, 224),
                    Color.WHITE,
                    new Color(168, 165, 223)
                );
            case FOG:
            default:
                return new WeatherTheme(
                    new Color(241, 244, 248),
                    new Color(205, 215, 226),
                    new Color(228, 234, 240),
                    new Color(66, 80, 98),
                    new Color(121, 134, 151),
                    new Color(53, 70, 92),
                    new Color(96, 112, 133),
                    new Color(109, 135, 177),
                    Color.WHITE,
                    new Color(192, 204, 220)
                );
        }
    }

    private void showError(String message) {
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setText(message);
    }

    private String buildLocation(WeatherResult weatherResult) {
        if (weatherResult.getCountry() == null || weatherResult.getCountry().trim().isEmpty()) {
            return weatherResult.getCity();
        }

        return weatherResult.getCity() + ", " + weatherResult.getCountry();
    }

    private String buildSunSummary(WeatherResult weatherResult) {
        StringBuilder summary = new StringBuilder();

        if (weatherResult.getSunrise() != null) {
            summary.append("Alba ").append(weatherResult.getSunrise());
        }

        if (weatherResult.getSunset() != null) {
            if (summary.length() > 0) {
                summary.append("<br/>");
            }
            summary.append("Tramonto ").append(weatherResult.getSunset());
        }

        if (weatherResult.getSunshineDurationHours() != null) {
            if (summary.length() > 0) {
                summary.append("<br/>");
            }
            summary.append("Sole ").append(formatValue(weatherResult.getSunshineDurationHours(), weatherResult.getSunshineDurationUnit()));
        }

        return summary.length() == 0 ? "--" : summary.toString();
    }

    private String toHtml(String text) {
        return "<html>" + text + "</html>";
    }

    private String formatValue(double value, String unit) {
        return String.format(Locale.ITALY, "%.1f %s", value, unit);
    }

    private static final class WeatherTheme {
        private final Color pageBackground;
        private final Color heroStart;
        private final Color heroEnd;
        private final Color headerPrimaryText;
        private final Color headerSecondaryText;
        private final Color heroPrimaryText;
        private final Color heroSecondaryText;
        private final Color buttonBackground;
        private final Color buttonForeground;
        private final Color fieldBorderColor;

        private WeatherTheme(
            Color pageBackground,
            Color heroStart,
            Color heroEnd,
            Color headerPrimaryText,
            Color headerSecondaryText,
            Color heroPrimaryText,
            Color heroSecondaryText,
            Color buttonBackground,
            Color buttonForeground,
            Color fieldBorderColor
        ) {
            this.pageBackground = pageBackground;
            this.heroStart = heroStart;
            this.heroEnd = heroEnd;
            this.headerPrimaryText = headerPrimaryText;
            this.headerSecondaryText = headerSecondaryText;
            this.heroPrimaryText = heroPrimaryText;
            this.heroSecondaryText = heroSecondaryText;
            this.buttonBackground = buttonBackground;
            this.buttonForeground = buttonForeground;
            this.fieldBorderColor = fieldBorderColor;
        }
    }
}
