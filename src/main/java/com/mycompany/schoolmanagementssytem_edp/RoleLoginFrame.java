package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class RoleLoginFrame extends JFrame {

    private final Role role;
    private final SchoolRepository repository;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private char passwordEchoChar;
    private boolean passwordVisible;

    protected RoleLoginFrame(Role role) {
        super(role.getLoginTitle());
        this.role = role;
        this.repository = new SchoolRepository();
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1180, 760);
        setLocationRelativeTo(null);

        JPanel root = AppChrome.createRootFrame(this);
        setContentPane(root);

        root.add(
                AppChrome.createBrandHeader(
                        role.getDisplayName() + " access portal",
                        () -> AppNavigator.openLogin(role, this)
                ),
                BorderLayout.NORTH
        );

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        root.add(centerPanel, BorderLayout.CENTER);

        JPanel card = AppTheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(470, 540));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(34, 34, 34, 34)
        ));
        centerPanel.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel(role.getLoginTitle().toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.displayFont(28));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(titleLabel, gbc);

        JLabel helperLabel = new JLabel("Use the account stored in the XAMPP database.");
        helperLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helperLabel.setFont(AppTheme.bodyFont(14));
        helperLabel.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(helperLabel, gbc);

        JLabel usernameLabel = new JLabel(role.getUsernameLabel());
        usernameLabel.setFont(AppTheme.bodyBoldFont(14));
        usernameLabel.setForeground(AppTheme.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(usernameLabel, gbc);

        usernameField = new JTextField();
        AppTheme.styleTextField(usernameField);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(AppTheme.bodyBoldFont(14));
        passwordLabel.setForeground(AppTheme.TEXT_SECONDARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 6, 0);
        card.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        AppTheme.styleTextField(passwordField);
        passwordEchoChar = passwordField.getEchoChar();
        JButton togglePasswordButton = new JButton("Show");
        AppTheme.styleSecondaryButton(togglePasswordButton);
        togglePasswordButton.addActionListener(event -> togglePasswordVisibility(togglePasswordButton));
        JPanel passwordPanel = new JPanel(new BorderLayout(8, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(passwordPanel, gbc);

        JLabel forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(AppTheme.bodyBoldFont(12));
        forgotPasswordLabel.setForeground(AppTheme.PRIMARY_ACTIVE);
        forgotPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotPasswordLabel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                handlePasswordReset();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                forgotPasswordLabel.setForeground(AppTheme.PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                forgotPasswordLabel.setForeground(AppTheme.PRIMARY_ACTIVE);
            }
        });
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(forgotPasswordLabel, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(AppTheme.bodyFont(12));
        statusLabel.setForeground(AppTheme.TEXT_MUTED);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(statusLabel, gbc);

        JButton signInButton = new JButton("Sign In");
        signInButton.setPreferredSize(new Dimension(0, 48));
        AppTheme.stylePrimaryButton(signInButton);
        signInButton.addActionListener(event -> attemptLogin());
        signInButton.addMouseListener(AppTheme.clickPulse(signInButton));
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(signInButton, gbc);

        JButton backButton = new JButton("Back");
        AppTheme.styleSecondaryButton(backButton);
        backButton.addActionListener(event -> AppNavigator.openRoleSelection(this));
        gbc.gridy = 9;
        gbc.insets = new Insets(12, 0, 0, 0);
        card.add(backButton, gbc);

        KeyAdapter clearStatusListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                statusLabel.setText(" ");
            }
        };

        usernameField.addKeyListener(clearStatusListener);
        passwordField.addKeyListener(clearStatusListener);
        usernameField.addActionListener(event -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(event -> attemptLogin());

        getRootPane().setDefaultButton(signInButton);
        getRootPane().registerKeyboardAction(
                event -> AppNavigator.openRoleSelection(this),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JPanel.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setForeground(AppTheme.DANGER);
            statusLabel.setText("Enter both " + role.getUsernameLabel().toLowerCase() + " and password.");
            return;
        }

        try {
            User user = repository.authenticate(role, username, password);

            if (user == null) {
                statusLabel.setForeground(AppTheme.DANGER);
                statusLabel.setText("Invalid credentials for the selected role.");
                JOptionPane.showMessageDialog(
                        this,
                        "Login failed. Please verify your role, username, and password.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            statusLabel.setForeground(AppTheme.SUCCESS);
            statusLabel.setText("Login successful. Loading dashboard...");
            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user.getDisplayName() + ".",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
            AppNavigator.openDashboard(role, user, this);
        } catch (SQLException exception) {
            statusLabel.setForeground(AppTheme.DANGER);
            statusLabel.setText("Database connection failed.");
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to the XAMPP/MySQL database.\n\n" + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handlePasswordReset() {
        String username = JOptionPane.showInputDialog(
                this,
                "Enter your " + role.getUsernameLabel() + ":",
                "Reset Password",
                JOptionPane.QUESTION_MESSAGE
        );
        if (username == null || username.isBlank()) {
            return;
        }

        String email = JOptionPane.showInputDialog(
                this,
                "Enter your registered email:",
                "Reset Password",
                JOptionPane.QUESTION_MESSAGE
        );
        if (email == null || email.isBlank()) {
            return;
        }

        JPasswordField newPasswordField = new JPasswordField();
        Object[] message = {
            "Enter your new password:", newPasswordField
        };
        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Reset Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(newPasswordField.getPassword()).trim();
        if (newPassword.isBlank()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean updated = repository.resetPassword(role, username.trim(), email.trim(), newPassword);
            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password updated successfully. You can sign in using the new password.",
                        "Password Updated",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "We could not verify that username/email combination for the selected role.",
                        "Reset Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to reset the password.\n\n" + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void togglePasswordVisibility(JButton togglePasswordButton) {
        passwordVisible = !passwordVisible;
        passwordField.setEchoChar(passwordVisible ? (char) 0 : passwordEchoChar);
        togglePasswordButton.setText(passwordVisible ? "Hide" : "Show");
    }
}
