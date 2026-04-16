package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public final class AppChrome {

    private AppChrome() {
    }

    public static BackgroundImagePanel createRootFrame(JFrame frame) {
        AppAssets.applyWindowIcon(frame);
        BackgroundImagePanel root = new BackgroundImagePanel();
        root.setLayout(new BorderLayout(0, 18));
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        return root;
    }

    public static JPanel createBrandHeader(String subtitle, Runnable themeToggleAction) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(AppTheme.HEADER_BACKGROUND);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel mainLogo = new JLabel(AppAssets.mainCampusLogo(54, 54));
        left.add(mainLogo);

        JPanel textPanel = new JPanel(new BorderLayout(0, 2));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("BulSU Portal");
        titleLabel.setFont(AppTheme.headingFont(20));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        textPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(AppTheme.bodyFont(13));
        subtitleLabel.setForeground(AppTheme.TEXT_MUTED);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);
        left.add(textPanel);

        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(new ThemeToggleButton(themeToggleAction));
        right.add(new JLabel(AppAssets.sarmientoLogo(54, 54)));
        header.add(right, BorderLayout.EAST);

        return header;
    }
}
