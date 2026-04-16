package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.JToggleButton;

public class ThemeToggleButton extends JToggleButton {

    private final Runnable onToggle;

    public ThemeToggleButton(Runnable onToggle) {
        this.onToggle = onToggle;
        setSelected(AppTheme.isDarkMode());
        setPreferredSize(new Dimension(78, 36));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        addActionListener(event -> {
            AppTheme.toggleMode();
            onToggle.run();
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 4;
        int knobSize = height - (padding * 2);
        int knobX = isSelected() ? width - knobSize - padding : padding;

        graphics2D.setColor(isSelected()
                ? new Color(14, 32, 47, 220)
                : new Color(238, 247, 255, 230));
        graphics2D.fillRoundRect(0, 0, width, height, height, height);

        graphics2D.setColor(AppTheme.BORDER);
        graphics2D.drawRoundRect(0, 0, width - 1, height - 1, height, height);

        paintSun(graphics2D, 18, height / 2, 7, isSelected() ? AppTheme.TEXT_MUTED : AppTheme.PRIMARY_ACTIVE);
        paintMoon(graphics2D, width - 18, height / 2, 7, isSelected() ? AppTheme.PRIMARY : AppTheme.TEXT_MUTED);

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillOval(knobX, padding, knobSize, knobSize);
        graphics2D.setColor(new Color(0, 0, 0, 28));
        graphics2D.drawOval(knobX, padding, knobSize, knobSize);

        graphics2D.dispose();
    }

    private void paintSun(Graphics2D graphics2D, int centerX, int centerY, int radius, Color color) {
        graphics2D.setColor(color);
        graphics2D.fillOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
        graphics2D.setStroke(new BasicStroke(1.6f));
        for (int angle = 0; angle < 360; angle += 45) {
            double radians = Math.toRadians(angle);
            int innerX = centerX + (int) (Math.cos(radians) * (radius + 1));
            int innerY = centerY + (int) (Math.sin(radians) * (radius + 1));
            int outerX = centerX + (int) (Math.cos(radians) * (radius + 4));
            int outerY = centerY + (int) (Math.sin(radians) * (radius + 4));
            graphics2D.drawLine(innerX, innerY, outerX, outerY);
        }
    }

    private void paintMoon(Graphics2D graphics2D, int centerX, int centerY, int radius, Color color) {
        Area moon = new Area(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0));
        moon.subtract(new Area(new Ellipse2D.Double(centerX - radius / 3.0, centerY - radius, radius * 2.0, radius * 2.0)));
        graphics2D.setColor(color);
        graphics2D.fill(moon);
    }
}
