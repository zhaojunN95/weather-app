package com.example.weatherapp.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class WeatherGradientPanel extends JPanel {

    private Color startColor;
    private Color endColor;

    public WeatherGradientPanel(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false);
    }

    public void setGradientColors(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
        graphics2D.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 26, 26));
        graphics2D.dispose();
        super.paintComponent(graphics);
    }
}
