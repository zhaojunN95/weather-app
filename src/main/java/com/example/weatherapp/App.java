package com.example.weatherapp;

import com.example.weatherapp.service.WeatherResult;
import com.example.weatherapp.service.WeatherResultFormatter;
import com.example.weatherapp.service.WeatherService;
import com.example.weatherapp.ui.WeatherAppFrame;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class App {

    public static void main(String[] args) {
        WeatherService weatherService = new WeatherService();

        if (shouldRunCli(args)) {
            runCli(weatherService);
            return;
        }

        runGui(weatherService);
    }

    private static boolean shouldRunCli(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            return true;
        }

        if (args == null) {
            return false;
        }

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) {
                return true;
            }
        }

        return false;
    }

    private static void runCli(WeatherService weatherService) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Inserisci il nome di una citta: ");
        String city = scanner.nextLine();

        try {
            WeatherResult weatherResult = weatherService.getWeather(city);
            System.out.println(WeatherResultFormatter.format(weatherResult));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Errore di rete durante il recupero dei dati meteo: " + ex.getMessage());
        }
    }

    private static void runGui(WeatherService weatherService) {
        CountDownLatch closed = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            try {
                WeatherAppFrame frame = new WeatherAppFrame(weatherService);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        closed.countDown();
                    }
                });
                frame.setVisible(true);
            } catch (Exception ex) {
                closed.countDown();
            }
        });

        try {
            closed.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
