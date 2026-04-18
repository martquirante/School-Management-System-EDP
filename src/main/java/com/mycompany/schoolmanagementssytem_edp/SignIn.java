package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class SignIn extends JFrame implements ThemeRefreshable {

    public SignIn() {
        super("Choose Your Role");
        buildUi();
    }

    private void buildUi() {
        getContentPane().removeAll();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        if (getWidth() <= 1 || getHeight() <= 1) {
            setSize(1180, 760);
            setLocationRelativeTo(null);
        }

        JPanel root = AppChrome.createRootFrame(this);
        setContentPane(root);

        root.add(
                AppChrome.createBrandHeader(
                        "Choose the portal role that matches your account",
                        () -> AppNavigator.refreshCurrentFrame(this)
                ),
                BorderLayout.NORTH
        );

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        JPanel card = AppTheme.createCardPanel();
        card.setPreferredSize(new Dimension(540, 560));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(42, 48, 42, 48)
        ));
        center.add(card);

        GridBagLayout layout = new GridBagLayout();
        card.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Choose Your Role");
        title.setFont(AppTheme.displayFont(28));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(title, gbc);

        JLabel helper = new JLabel("The selected role opens its own login form and dashboard.");
        helper.setFont(AppTheme.bodyFont(14));
        helper.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(helper, gbc);

        addRoleButton(card, gbc, 2, Role.STAFF);
        addRoleButton(card, gbc, 3, Role.STUDENT);
        addRoleButton(card, gbc, 4, Role.PROFESSOR);
        addRoleButton(card, gbc, 5, Role.ADMIN);

        JButton backButton = new JButton("Back");
        AppTheme.styleSecondaryButton(backButton);
        backButton.addActionListener(event -> AppNavigator.openLanding(this));
        gbc.gridy = 6;
        gbc.insets = new Insets(28, 0, 0, 0);
        card.add(backButton, gbc);

        getRootPane().registerKeyboardAction(
                event -> AppNavigator.openLanding(this),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JPanel.WHEN_IN_FOCUSED_WINDOW
        );
        revalidate();
        repaint();
    }

    private void addRoleButton(JPanel card, GridBagConstraints gbc, int row, Role role) {
        JButton button = new JButton(role.getDisplayName().toUpperCase());
        button.setPreferredSize(new Dimension(0, 52));
        AppTheme.stylePrimaryButton(button);
        button.addActionListener(event -> AppNavigator.openLogin(role, this));
        button.addMouseListener(AppTheme.clickPulse(button));
        gbc.gridy = row;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(button, gbc);
    }

    @Override
    public void refreshTheme() {
        AppTheme.refreshFrame(this, this::buildUi);
    }
}
