package com.mycompany.schoolmanagementssytem_edp;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public final class AppTheme {

    public static Color SURFACE_BACKGROUND;
    public static Color PANEL_BACKGROUND;
    public static Color SIDEBAR_BACKGROUND;
    public static Color SECTION_BACKGROUND;
    public static Color PRIMARY;
    public static Color PRIMARY_HOVER;
    public static Color PRIMARY_ACTIVE;
    public static Color PRIMARY_SOFT;
    public static Color BORDER;
    public static Color TEXT_PRIMARY;
    public static Color TEXT_SECONDARY;
    public static Color TEXT_MUTED;
    public static Color DANGER;
    public static Color SUCCESS;
    public static Color INPUT_BACKGROUND;
    public static Color TABLE_BACKGROUND;
    public static Color TABLE_ALT_BACKGROUND;
    public static Color TABLE_SELECTION;
    public static Color TABLE_SELECTION_TEXT;
    public static Color HEADER_BACKGROUND;
    public static Color OVERLAY_TOP;
    public static Color OVERLAY_BOTTOM;

    private static boolean darkMode;

    private AppTheme() {
    }

    public static void install() {
        refreshPalette();

        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ignored) {
            // FlatLaf is optional at runtime if the environment cannot load it.
        }

        UIManager.put("Button.arc", 22);
        UIManager.put("Component.arc", 18);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void toggleMode() {
        darkMode = !darkMode;
        install();
    }

    public static void refreshFrame(JFrame frame, Runnable rebuildAction) {
        Rectangle bounds = frame.getBounds();
        int extendedState = frame.getExtendedState();
        boolean visible = frame.isVisible();

        install();
        rebuildAction.run();

        frame.setBounds(bounds);
        frame.setExtendedState(extendedState);
        SwingUtilities.updateComponentTreeUI(frame);
        frame.revalidate();
        frame.repaint();
        if (visible) {
            frame.setVisible(true);
        }
    }

    public static void refreshPalette() {
        if (darkMode) {
            SURFACE_BACKGROUND = new Color(10, 18, 28, 190);
            PANEL_BACKGROUND = new Color(11, 24, 37, 215);
            SIDEBAR_BACKGROUND = new Color(8, 20, 31, 220);
            SECTION_BACKGROUND = new Color(12, 26, 39, 175);
            PRIMARY = new Color(72, 165, 255);
            PRIMARY_HOVER = new Color(92, 181, 255);
            PRIMARY_ACTIVE = new Color(52, 146, 236);
            PRIMARY_SOFT = new Color(18, 44, 67, 225);
            BORDER = new Color(69, 102, 131, 210);
            TEXT_PRIMARY = new Color(234, 244, 255);
            TEXT_SECONDARY = new Color(189, 214, 239);
            TEXT_MUTED = new Color(141, 171, 201);
            DANGER = new Color(255, 116, 116);
            SUCCESS = new Color(104, 214, 143);
            INPUT_BACKGROUND = new Color(7, 18, 29, 235);
            TABLE_BACKGROUND = new Color(8, 20, 31, 220);
            TABLE_ALT_BACKGROUND = new Color(13, 28, 42, 220);
            TABLE_SELECTION = new Color(40, 94, 145, 220);
            TABLE_SELECTION_TEXT = Color.WHITE;
            HEADER_BACKGROUND = new Color(7, 17, 28, 215);
            OVERLAY_TOP = new Color(5, 12, 20, 118);
            OVERLAY_BOTTOM = new Color(5, 12, 20, 160);
        } else {
            SURFACE_BACKGROUND = new Color(255, 255, 255, 160);
            PANEL_BACKGROUND = new Color(255, 255, 255, 225);
            SIDEBAR_BACKGROUND = new Color(244, 250, 255, 230);
            SECTION_BACKGROUND = new Color(250, 253, 255, 185);
            PRIMARY = new Color(96, 176, 255);
            PRIMARY_HOVER = new Color(72, 160, 247);
            PRIMARY_ACTIVE = new Color(48, 141, 238);
            PRIMARY_SOFT = new Color(231, 244, 255, 235);
            BORDER = new Color(216, 231, 244, 230);
            TEXT_PRIMARY = new Color(28, 46, 68);
            TEXT_SECONDARY = new Color(72, 96, 124);
            TEXT_MUTED = new Color(113, 133, 156);
            DANGER = new Color(203, 70, 70);
            SUCCESS = new Color(55, 144, 88);
            INPUT_BACKGROUND = new Color(255, 255, 255, 235);
            TABLE_BACKGROUND = new Color(255, 255, 255, 215);
            TABLE_ALT_BACKGROUND = new Color(244, 250, 255, 215);
            TABLE_SELECTION = new Color(96, 176, 255, 210);
            TABLE_SELECTION_TEXT = Color.WHITE;
            HEADER_BACKGROUND = new Color(255, 255, 255, 225);
            OVERLAY_TOP = new Color(255, 255, 255, 86);
            OVERLAY_BOTTOM = new Color(245, 250, 255, 140);
        }
    }

    public static Font displayFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public static Font headingFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public static Font bodyFont(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }

    public static Font bodyBoldFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(cardBorder());
        return panel;
    }

    public static JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(cardBorder());
        return panel;
    }

    public static Border cardBorder() {
        return new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(18, 18, 18, 18)
        );
    }

    public static void stylePrimaryButton(AbstractButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setFont(bodyBoldFont(16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(button, PRIMARY, PRIMARY_HOVER);
    }

    public static void styleSecondaryButton(AbstractButton button) {
        button.setBackground(PANEL_BACKGROUND);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 18, 12, 18)
        ));
        button.setFont(bodyBoldFont(15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(button, PANEL_BACKGROUND, PRIMARY_SOFT);
    }

    public static void styleDangerButton(AbstractButton button) {
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setFont(bodyBoldFont(15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHover(button, DANGER, DANGER.brighter());
    }

    public static void styleNavigationButton(AbstractButton button) {
        button.setBackground(SIDEBAR_BACKGROUND);
        button.setForeground(TEXT_SECONDARY);
        button.setBorder(new EmptyBorder(14, 18, 14, 18));
        button.setFont(bodyBoldFont(14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void setNavigationActive(AbstractButton button, boolean active) {
        if (active) {
            button.setBackground(PRIMARY_SOFT);
            button.setForeground(PRIMARY_ACTIVE);
        } else {
            button.setBackground(SIDEBAR_BACKGROUND);
            button.setForeground(TEXT_SECONDARY);
        }
    }

    public static void styleTextField(JTextField field) {
        field.setFont(bodyFont(15));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(INPUT_BACKGROUND);
        field.setCaretColor(TEXT_PRIMARY);
        field.setSelectionColor(PRIMARY);
        field.setSelectedTextColor(Color.WHITE);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(0, 44));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(bodyFont(15));
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        comboBox.setFocusable(false);
    }

    public static void styleTable(JTable table) {
        table.setBackground(TABLE_BACKGROUND);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TABLE_SELECTION_TEXT);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setRowMargin(0);
        table.getTableHeader().setBackground(HEADER_BACKGROUND);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    public static JLabel createMutedLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(bodyFont(size));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static MouseAdapter clickPulse(JButton button) {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                button.setBackground(PRIMARY_ACTIVE);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                button.setBackground(PRIMARY_HOVER);
            }
        };
    }

    private static void installHover(AbstractButton button, Color baseColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(baseColor);
            }
        });
    }

    public static void setSectionPadding(JComponent component) {
        component.setBorder(new EmptyBorder(18, 18, 18, 18));
    }
}
