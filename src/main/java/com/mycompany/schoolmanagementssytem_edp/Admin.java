package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Admin extends JFrame {

    public Admin() {
        super("BulSU School Management System");
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 820);
        setLocationRelativeTo(null);

        JPanel root = AppChrome.createRootFrame(this);
        setContentPane(root);

        root.add(
                AppChrome.createBrandHeader(
                        "Smart campus access for students, faculty, staff, and administrators",
                        () -> AppNavigator.openLanding(this)
                ),
                BorderLayout.NORTH
        );

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        JPanel heroCard = AppTheme.createCardPanel();
        heroCard.setLayout(new GridBagLayout());
        heroCard.setPreferredSize(new Dimension(820, 470));
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(48, 50, 48, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel badgeLabel = new JLabel("BulSU Sarmiento Campus Digital Portal");
        badgeLabel.setFont(AppTheme.bodyBoldFont(14));
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(AppTheme.PRIMARY_SOFT);
        badgeLabel.setForeground(AppTheme.PRIMARY_ACTIVE);
        badgeLabel.setBorder(new EmptyBorder(10, 16, 10, 16));
        heroCard.add(badgeLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 10, 0);
        JLabel subtitleLabel = new JLabel("Welcome to the");
        subtitleLabel.setFont(AppTheme.bodyFont(24));
        subtitleLabel.setForeground(AppTheme.TEXT_MUTED);
        heroCard.add(subtitleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 26, 0);
        JLabel titleLabel = new JLabel("School Management System");
        titleLabel.setFont(AppTheme.displayFont(44));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        heroCard.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 18, 0);
        JLabel descriptionLabel = new JLabel(
                "<html><div style='text-align:center;'>"
                + "Code-first Java Swing screens, role-based logins, and database-driven dashboards "
                + "for Admin, Student, Professor, and Staff."
                + "</div></html>"
        );
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(AppTheme.bodyFont(17));
        descriptionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        heroCard.add(descriptionLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(12, 0, 0, 0);
        JButton startButton = new JButton("Let's Get Started");
        startButton.setPreferredSize(new Dimension(260, 52));
        AppTheme.stylePrimaryButton(startButton);
        startButton.addActionListener(this::openRoleSelection);
        startButton.addMouseListener(AppTheme.clickPulse(startButton));
        heroCard.add(startButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 0, 0);
        JLabel footerNote = new JLabel("Responsive Java Swing UI with XAMPP/MySQL-powered role dashboards.");
        footerNote.setFont(AppTheme.bodyFont(14));
        footerNote.setForeground(AppTheme.TEXT_MUTED);
        heroCard.add(footerNote, gbc);

        center.add(heroCard);
        getRootPane().setDefaultButton(startButton);
        getRootPane().registerKeyboardAction(
                event -> openRoleSelection(null),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JPanel.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void openRoleSelection(ActionEvent event) {
        AppNavigator.openRoleSelection(this);
    }
}
