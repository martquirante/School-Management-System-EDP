package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class AccountFormPanel extends JPanel {

    private final JTextField usernameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JTextField fullNameField;
    private final JComboBox<Role> roleComboBox;
    private final JTextField linkedIdField;
    private final JLabel linkedIdHelperLabel;
    private final Integer existingUserId;
    private final char passwordEchoChar;
    private boolean passwordVisible;

    AccountFormPanel(User existingUser) {
        this.existingUserId = existingUser == null ? null : existingUser.getUserId();
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        passwordEchoChar = passwordField.getEchoChar();
        fullNameField = new JTextField(20);
        linkedIdField = new JTextField(20);

        AppTheme.styleTextField(usernameField);
        AppTheme.styleTextField(emailField);
        AppTheme.styleTextField(passwordField);
        AppTheme.styleTextField(fullNameField);
        AppTheme.styleTextField(linkedIdField);

        roleComboBox = new JComboBox<>(new DefaultComboBoxModel<>(Role.values()));
        AppTheme.styleComboBox(roleComboBox);
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Role selectedRole) {
                    label.setText(selectedRole.getDisplayName());
                }
                return label;
            }
        });

        linkedIdHelperLabel = new JLabel();
        linkedIdHelperLabel.setFont(AppTheme.bodyFont(12));
        linkedIdHelperLabel.setForeground(AppTheme.TEXT_MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        addField("Username", usernameField, gbc);
        addField("Email", emailField, gbc);
        JButton togglePasswordButton = new JButton("Show");
        AppTheme.styleSecondaryButton(togglePasswordButton);
        togglePasswordButton.addActionListener(event -> togglePasswordVisibility(togglePasswordButton));
        JPanel passwordPanel = new JPanel(new java.awt.BorderLayout(8, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, java.awt.BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, java.awt.BorderLayout.EAST);
        addField("Password", passwordPanel, gbc);
        addField("Display Name", fullNameField, gbc);
        addField("Role", roleComboBox, gbc);
        addField("Linked Reference ID", linkedIdField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(linkedIdHelperLabel, gbc);

        roleComboBox.addActionListener(event -> updateLinkedIdHelper());

        if (existingUser != null) {
            usernameField.setText(existingUser.getUsername());
            emailField.setText(existingUser.getEmail());
            passwordField.setText(existingUser.getPassword());
            fullNameField.setText(existingUser.getFullName());

            Role selectedRole = Role.fromDatabaseValue(existingUser.getRole());
            roleComboBox.setSelectedItem(selectedRole);
            Integer linkedId = switch (selectedRole) {
                case STUDENT -> existingUser.getStudentId();
                case PROFESSOR -> existingUser.getProfessorId();
                case ADMIN -> existingUser.getAdminId();
                case STAFF -> existingUser.getStaffId();
            };
            linkedIdField.setText(linkedId == null ? "" : String.valueOf(linkedId));
        } else {
            roleComboBox.setSelectedItem(Role.STUDENT);
        }

        updateLinkedIdHelper();
    }

    private void addField(String labelText, Component field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.bodyBoldFont(13));
        label.setForeground(AppTheme.TEXT_SECONDARY);
        add(label, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 12, 0);
        add(field, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
    }

    private void updateLinkedIdHelper() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        String helperText = switch (selectedRole == null ? Role.STUDENT : selectedRole) {
            case STUDENT -> "Maps to student_id in the users table.";
            case PROFESSOR -> "Maps to professor_id in the users table.";
            case ADMIN -> "Maps to admin_id in the users table.";
            case STAFF -> "Maps to staff_id in the users table.";
        };

        linkedIdHelperLabel.setText(helperText);
        linkedIdField.setToolTipText(helperText);
    }

    private void togglePasswordVisibility(JButton togglePasswordButton) {
        passwordVisible = !passwordVisible;
        passwordField.setEchoChar(passwordVisible ? (char) 0 : passwordEchoChar);
        togglePasswordButton.setText(passwordVisible ? "Hide" : "Show");
    }

    User buildUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String linkedIdText = linkedIdField.getText().trim();
        Role selectedRole = (Role) roleComboBox.getSelectedItem();

        if (username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (selectedRole == null) {
            throw new IllegalArgumentException("Select a role for the account.");
        }

        Integer linkedId = null;
        if (!linkedIdText.isBlank()) {
            try {
                linkedId = Integer.valueOf(linkedIdText);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Linked reference ID must be numeric.");
            }
        }

        User builtUser = new User();
        if (existingUserId != null) {
            builtUser.setUserId(existingUserId);
        }
        builtUser.setUsername(username);
        builtUser.setEmail(email);
        builtUser.setPassword(password);
        builtUser.setRole(selectedRole.getDatabaseValue());
        builtUser.setFullName(fullName.isBlank() ? null : fullName);

        switch (selectedRole) {
            case STUDENT -> builtUser.setStudentId(linkedId);
            case PROFESSOR -> builtUser.setProfessorId(linkedId);
            case ADMIN -> builtUser.setAdminId(linkedId);
            case STAFF -> builtUser.setStaffId(linkedId);
        }

        return builtUser;
    }
}
