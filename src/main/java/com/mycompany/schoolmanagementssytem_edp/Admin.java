package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

public class Admin extends JFrame implements ThemeRefreshable {

    public Admin() {
        super("BulSU School Management System");
        buildUi();
    }

    private void buildUi() {
        getContentPane().removeAll();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        if (getWidth() <= 1 || getHeight() <= 1) {
            setSize(1280, 820);
            setLocationRelativeTo(null);
        }

        JPanel root = AppChrome.createRootFrame(this);
        setContentPane(root);

        root.add(
                AppChrome.createBrandHeader(
                        "Academic records, class management, and student services in one BulSU portal",
                        () -> AppNavigator.refreshCurrentFrame(this)
                ),
                BorderLayout.NORTH
        );

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        JPanel heroCard = AppTheme.createCardPanel();
        heroCard.setLayout(new BorderLayout(0, 24));
        heroCard.setPreferredSize(new Dimension(920, 560));
        heroCard.setBackground(AppTheme.PRIMARY_ACTIVE);
        heroCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.PRIMARY_HOVER, 1),
                new EmptyBorder(42, 44, 42, 44)
        ));

        JPanel heroTop = new JPanel(new BorderLayout(0, 14));
        heroTop.setOpaque(false);
        JLabel badgeLabel = new JLabel("Welcome to");
        badgeLabel.setFont(AppTheme.bodyFont(28));
        badgeLabel.setForeground(java.awt.Color.WHITE);
        heroTop.add(badgeLabel, BorderLayout.NORTH);

        JPanel titlePanel = new JPanel(new BorderLayout(0, 8));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Bulacan State University");
        titleLabel.setFont(AppTheme.displayFont(58));
        titleLabel.setForeground(java.awt.Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        JLabel descriptionLabel = new JLabel(
                "<html><div style='width:760px;'>"
                + "Academic records, class management, grading, and student services in one presentation-ready BulSU portal."
                + "</div></html>"
        );
        descriptionLabel.setFont(AppTheme.bodyFont(18));
        descriptionLabel.setForeground(new java.awt.Color(236, 244, 255));
        titlePanel.add(descriptionLabel, BorderLayout.SOUTH);
        heroTop.add(titlePanel, BorderLayout.CENTER);
        heroCard.add(heroTop, BorderLayout.NORTH);

        JPanel statementsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        statementsPanel.setOpaque(false);
        statementsPanel.add(createStatementBlock(
                "Vision",
                "Bulacan State University is a progressive knowledge-generating institution globally recognized for excellent instruction, pioneering research, and responsive extension services."
        ));
        statementsPanel.add(createStatementBlock(
                "Mission",
                "Bulacan State University exists to produce highly competent, ethical, and service-oriented professionals through quality and inclusive education, relevant innovation, and community engagement."
        ));
        heroCard.add(statementsPanel, BorderLayout.CENTER);

        JPanel heroBottom = new JPanel(new BorderLayout(0, 12));
        heroBottom.setOpaque(false);
        JButton startButton = new JButton("Open the Portal");
        startButton.setPreferredSize(new Dimension(260, 52));
        AppTheme.styleSecondaryButton(startButton);
        startButton.setBackground(java.awt.Color.WHITE);
        startButton.setForeground(AppTheme.PRIMARY_ACTIVE);
        startButton.addActionListener(this::openRoleSelection);
        startButton.addMouseListener(AppTheme.clickPulse(startButton));
        heroBottom.add(startButton, BorderLayout.WEST);
        heroCard.add(heroBottom, BorderLayout.SOUTH);

        center.add(heroCard);
        getRootPane().setDefaultButton(startButton);
        getRootPane().registerKeyboardAction(
                event -> openRoleSelection(null),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JPanel.WHEN_IN_FOCUSED_WINDOW
        );
        revalidate();
        repaint();
    }

    private JPanel createStatementBlock(String title, String body) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.headingFont(26));
        titleLabel.setForeground(java.awt.Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel bodyLabel = new JLabel("<html><div style='width:320px;'>" + body + "</div></html>");
        bodyLabel.setFont(AppTheme.bodyFont(16));
        bodyLabel.setForeground(new java.awt.Color(236, 244, 255));
        bodyLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(bodyLabel, BorderLayout.CENTER);
        return card;
    }

    private void openRoleSelection(ActionEvent event) {
        AppNavigator.openRoleSelection(this);
    }

    @Override
    public void refreshTheme() {
        AppTheme.refreshFrame(this, this::buildUi);
    }
}
