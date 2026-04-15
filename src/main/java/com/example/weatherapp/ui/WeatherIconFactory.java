package com.example.weatherapp.ui;

import com.example.weatherapp.service.WeatherVisualType;

import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public final class WeatherIconFactory {

    private WeatherIconFactory() {
    }

    public static ImageIcon createAppIcon(int size) {
        return new ImageIcon(createAppIconImage(size));
    }

    public static BufferedImage createAppIconImage(int size) {
        BufferedImage image = createCanvas(size);
        Graphics2D graphics = image.createGraphics();

        applyRenderingHints(graphics);
        paintSkyBackground(graphics, size, true);
        paintSun(graphics, size, 0.30, 0.30, size * 0.17);
        paintCloud(graphics, size, 0.54, 0.58, 0.74, new Color(255, 255, 255, 240));

        graphics.dispose();
        return image;
    }

    public static ImageIcon createConditionIcon(WeatherVisualType visualType, boolean isDay, int size) {
        BufferedImage image = createCanvas(size);
        Graphics2D graphics = image.createGraphics();

        applyRenderingHints(graphics);
        paintSkyBackground(graphics, size, isDay);

        WeatherVisualType resolvedType = visualType == null ? WeatherVisualType.PARTLY_CLOUDY : visualType;

        switch (resolvedType) {
            case CLEAR:
                if (isDay) {
                    paintSun(graphics, size, 0.50, 0.48, size * 0.22);
                } else {
                    paintMoon(graphics, size, 0.50, 0.48, size * 0.19);
                }
                break;
            case PARTLY_CLOUDY:
                if (isDay) {
                    paintSun(graphics, size, 0.38, 0.34, size * 0.16);
                } else {
                    paintMoon(graphics, size, 0.38, 0.34, size * 0.14);
                }
                paintCloud(graphics, size, 0.57, 0.60, 0.78, new Color(255, 255, 255, 240));
                break;
            case CLOUDY:
                paintCloud(graphics, size, 0.50, 0.55, 0.82, new Color(236, 242, 250));
                paintCloud(graphics, size, 0.62, 0.66, 0.62, new Color(214, 224, 238));
                break;
            case FOG:
                paintCloud(graphics, size, 0.53, 0.49, 0.72, new Color(240, 244, 248));
                paintFog(graphics, size);
                break;
            case DRIZZLE:
                paintCloud(graphics, size, 0.54, 0.50, 0.78, new Color(245, 247, 251));
                paintRain(graphics, size, 3, new Color(93, 164, 255));
                break;
            case RAIN:
                paintCloud(graphics, size, 0.54, 0.50, 0.80, new Color(238, 242, 248));
                paintRain(graphics, size, 4, new Color(66, 149, 255));
                break;
            case SNOW:
                paintCloud(graphics, size, 0.54, 0.50, 0.80, new Color(245, 247, 251));
                paintSnow(graphics, size);
                break;
            case THUNDERSTORM:
                paintCloud(graphics, size, 0.54, 0.48, 0.82, new Color(214, 223, 239));
                paintRain(graphics, size, 2, new Color(77, 158, 255));
                paintLightning(graphics, size);
                break;
            default:
                break;
        }

        graphics.dispose();
        return new ImageIcon(image);
    }

    public static ImageIcon createMetricIcon(String metricKey, int size) {
        BufferedImage image = createCanvas(size);
        Graphics2D graphics = image.createGraphics();

        applyRenderingHints(graphics);

        if ("temperature".equals(metricKey)) {
            paintThermometer(graphics, size, new Color(255, 128, 94));
        } else if ("humidity".equals(metricKey)) {
            paintHumidityDroplet(graphics, size);
        } else if ("rain".equals(metricKey)) {
            paintDroplet(graphics, size, new Color(56, 142, 255));
        } else if ("precipitation".equals(metricKey)) {
            paintDroplet(graphics, size, new Color(93, 164, 255));
            paintCloud(graphics, size, 0.52, 0.32, 0.50, new Color(220, 233, 248));
        } else if ("cloud".equals(metricKey)) {
            paintCloud(graphics, size, 0.52, 0.55, 0.78, new Color(205, 220, 239));
        } else if ("wind".equals(metricKey)) {
            paintWind(graphics, size);
        } else if ("showers".equals(metricKey)) {
            paintCloud(graphics, size, 0.52, 0.38, 0.64, new Color(221, 233, 248));
            paintRain(graphics, size, 3, new Color(70, 145, 255));
        } else if ("snow".equals(metricKey)) {
            paintSnow(graphics, size);
        } else if ("sun".equals(metricKey)) {
            paintSunrise(graphics, size);
        } else {
            paintSun(graphics, size, 0.50, 0.50, size * 0.18);
        }

        graphics.dispose();
        return new ImageIcon(image);
    }

    private static BufferedImage createCanvas(int size) {
        return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    }

    private static void applyRenderingHints(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private static void paintSkyBackground(Graphics2D graphics, int size, boolean isDay) {
        Color top = isDay ? new Color(127, 191, 255) : new Color(38, 56, 105);
        Color bottom = isDay ? new Color(228, 243, 255) : new Color(78, 104, 160);
        graphics.setPaint(new GradientPaint(0, 0, top, 0, size, bottom));
        graphics.fill(new RoundRectangle2D.Double(0, 0, size, size, size * 0.28, size * 0.28));
    }

    private static void paintSun(Graphics2D graphics, int size, double centerXFactor, double centerYFactor, double radius) {
        double centerX = size * centerXFactor;
        double centerY = size * centerYFactor;

        graphics.setStroke(new BasicStroke(Math.max(2f, size * 0.025f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(255, 215, 90, 220));

        for (int ray = 0; ray < 8; ray++) {
            double angle = Math.toRadians(ray * 45);
            int startX = (int) Math.round(centerX + Math.cos(angle) * radius * 1.35);
            int startY = (int) Math.round(centerY + Math.sin(angle) * radius * 1.35);
            int endX = (int) Math.round(centerX + Math.cos(angle) * radius * 1.85);
            int endY = (int) Math.round(centerY + Math.sin(angle) * radius * 1.85);
            graphics.drawLine(startX, startY, endX, endY);
        }

        graphics.setColor(new Color(255, 213, 79));
        graphics.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2));
    }

    private static void paintMoon(Graphics2D graphics, int size, double centerXFactor, double centerYFactor, double radius) {
        double centerX = size * centerXFactor;
        double centerY = size * centerYFactor;

        graphics.setColor(new Color(255, 240, 200));
        graphics.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2));

        graphics.setColor(new Color(58, 81, 138));
        graphics.fill(new Ellipse2D.Double(centerX - radius * 0.35, centerY - radius * 0.95, radius * 2, radius * 2));
    }

    private static void paintCloud(Graphics2D graphics, int size, double centerXFactor, double centerYFactor, double widthFactor, Color color) {
        double width = size * widthFactor;
        double height = width * 0.40;
        double x = size * centerXFactor - width / 2;
        double y = size * centerYFactor - height / 2;

        graphics.setColor(color);
        graphics.fill(new Ellipse2D.Double(x + width * 0.10, y + height * 0.22, width * 0.28, height * 0.55));
        graphics.fill(new Ellipse2D.Double(x + width * 0.28, y, width * 0.34, height * 0.72));
        graphics.fill(new Ellipse2D.Double(x + width * 0.50, y + height * 0.16, width * 0.30, height * 0.58));
        graphics.fill(new RoundRectangle2D.Double(x + width * 0.10, y + height * 0.34, width * 0.70, height * 0.36, height * 0.36, height * 0.36));
    }

    private static void paintRain(Graphics2D graphics, int size, int drops, Color color) {
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(Math.max(2f, size * 0.025f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int index = 0; index < drops; index++) {
            double offset = (index - (drops - 1) / 2.0) * size * 0.12;
            int startX = (int) Math.round(size * 0.52 + offset);
            int startY = (int) Math.round(size * 0.66);
            int endX = (int) Math.round(startX - size * 0.05);
            int endY = (int) Math.round(size * 0.84);
            graphics.drawLine(startX, startY, endX, endY);
        }
    }

    private static void paintSnow(Graphics2D graphics, int size) {
        graphics.setColor(new Color(255, 255, 255, 240));
        graphics.setStroke(new BasicStroke(Math.max(1.6f, size * 0.018f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int index = 0; index < 3; index++) {
            double centerX = size * (0.38 + index * 0.12);
            double centerY = size * 0.78;
            double radius = size * 0.035;

            graphics.drawLine((int) (centerX - radius), (int) centerY, (int) (centerX + radius), (int) centerY);
            graphics.drawLine((int) centerX, (int) (centerY - radius), (int) centerX, (int) (centerY + radius));
            graphics.drawLine((int) (centerX - radius * 0.7), (int) (centerY - radius * 0.7), (int) (centerX + radius * 0.7), (int) (centerY + radius * 0.7));
            graphics.drawLine((int) (centerX - radius * 0.7), (int) (centerY + radius * 0.7), (int) (centerX + radius * 0.7), (int) (centerY - radius * 0.7));
        }
    }

    private static void paintLightning(Graphics2D graphics, int size) {
        Path2D bolt = new Path2D.Double();
        bolt.moveTo(size * 0.53, size * 0.60);
        bolt.lineTo(size * 0.45, size * 0.80);
        bolt.lineTo(size * 0.54, size * 0.80);
        bolt.lineTo(size * 0.48, size * 0.94);
        bolt.lineTo(size * 0.66, size * 0.70);
        bolt.lineTo(size * 0.56, size * 0.70);
        bolt.closePath();

        graphics.setColor(new Color(255, 220, 85));
        graphics.fill(bolt);
    }

    private static void paintFog(Graphics2D graphics, int size) {
        graphics.setColor(new Color(255, 255, 255, 180));
        graphics.setStroke(new BasicStroke(Math.max(2f, size * 0.02f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int row = 0; row < 3; row++) {
            int y = (int) Math.round(size * (0.68 + row * 0.08));
            graphics.drawLine((int) Math.round(size * 0.26), y, (int) Math.round(size * 0.74), y);
        }
    }

    private static void paintThermometer(Graphics2D graphics, int size, Color color) {
        graphics.setColor(new Color(224, 234, 246));
        graphics.fill(new RoundRectangle2D.Double(size * 0.42, size * 0.12, size * 0.16, size * 0.56, size * 0.16, size * 0.16));

        graphics.setColor(color);
        graphics.fill(new Ellipse2D.Double(size * 0.28, size * 0.58, size * 0.44, size * 0.44));
        graphics.fill(new RoundRectangle2D.Double(size * 0.46, size * 0.22, size * 0.08, size * 0.52, size * 0.08, size * 0.08));
    }

    private static void paintDroplet(Graphics2D graphics, int size, Color color) {
        Path2D drop = new Path2D.Double();
        drop.moveTo(size * 0.50, size * 0.08);
        drop.curveTo(size * 0.72, size * 0.36, size * 0.78, size * 0.50, size * 0.78, size * 0.64);
        drop.curveTo(size * 0.78, size * 0.84, size * 0.64, size * 0.94, size * 0.50, size * 0.94);
        drop.curveTo(size * 0.36, size * 0.94, size * 0.22, size * 0.84, size * 0.22, size * 0.64);
        drop.curveTo(size * 0.22, size * 0.50, size * 0.28, size * 0.36, size * 0.50, size * 0.08);
        drop.closePath();

        graphics.setColor(color);
        graphics.fill(drop);
    }

    private static void paintWind(Graphics2D graphics, int size) {
        graphics.setColor(new Color(80, 157, 243));
        graphics.setStroke(new BasicStroke(Math.max(2.2f, size * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        graphics.draw(new QuadCurve2D.Double(size * 0.16, size * 0.35, size * 0.48, size * 0.20, size * 0.82, size * 0.35));
        graphics.draw(new QuadCurve2D.Double(size * 0.22, size * 0.56, size * 0.52, size * 0.42, size * 0.76, size * 0.56));
        graphics.draw(new QuadCurve2D.Double(size * 0.30, size * 0.76, size * 0.54, size * 0.66, size * 0.70, size * 0.76));
    }

    private static void paintHumidityDroplet(Graphics2D graphics, int size) {
        // Goccia acqua principale
        Path2D drop = new Path2D.Double();
        drop.moveTo(size * 0.50, size * 0.10);
        drop.curveTo(size * 0.70, size * 0.36, size * 0.76, size * 0.50, size * 0.76, size * 0.62);
        drop.curveTo(size * 0.76, size * 0.82, size * 0.64, size * 0.92, size * 0.50, size * 0.92);
        drop.curveTo(size * 0.36, size * 0.92, size * 0.24, size * 0.82, size * 0.24, size * 0.62);
        drop.curveTo(size * 0.24, size * 0.50, size * 0.30, size * 0.36, size * 0.50, size * 0.10);
        drop.closePath();

        graphics.setPaint(new GradientPaint(
            (float) (size * 0.30), (float) (size * 0.20), new Color(100, 200, 255),
            (float) (size * 0.70), (float) (size * 0.80), new Color(30, 130, 220)));
        graphics.fill(drop);

        // Riflesso per effetto 3D
        graphics.setColor(new Color(255, 255, 255, 100));
        graphics.fill(new Ellipse2D.Double(size * 0.36, size * 0.30, size * 0.14, size * 0.22));
    }

    private static void paintSunrise(Graphics2D graphics, int size) {
        graphics.setColor(new Color(132, 164, 207));
        graphics.setStroke(new BasicStroke(Math.max(2f, size * 0.08f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine((int) Math.round(size * 0.16), (int) Math.round(size * 0.72), (int) Math.round(size * 0.84), (int) Math.round(size * 0.72));

        graphics.setColor(new Color(255, 189, 80));
        graphics.fill(new Arc2D.Double(size * 0.26, size * 0.34, size * 0.48, size * 0.48, 0, 180, Arc2D.PIE));
    }
}
