package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class BackgroundImagePanel extends JPanel {

    public BackgroundImagePanel() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImage image = AppAssets.backgroundImage();
        if (image != null) {
            drawCoverImage(graphics2D, image);
        }

        graphics2D.setPaint(new java.awt.GradientPaint(
                0, 0, AppTheme.OVERLAY_TOP,
                0, getHeight(), AppTheme.OVERLAY_BOTTOM
        ));
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.dispose();
    }

    private void drawCoverImage(Graphics2D graphics2D, BufferedImage image) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (panelWidth <= 0 || panelHeight <= 0) {
            return;
        }

        double scale = Math.max(
                (double) panelWidth / image.getWidth(),
                (double) panelHeight / image.getHeight()
        );
        int drawWidth = (int) Math.round(image.getWidth() * scale);
        int drawHeight = (int) Math.round(image.getHeight() * scale);
        int x = (panelWidth - drawWidth) / 2;
        int y = (panelHeight - drawHeight) / 2;

        graphics2D.drawImage(image.getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH), x, y, null);
    }
}
