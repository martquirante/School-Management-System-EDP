package com.mycompany.schoolmanagementssytem_edp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class EnrollmentStatusFormPanel extends JPanel {

    private final JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"enrolled", "completed", "dropped"});

    EnrollmentStatusFormPanel(String currentStatus) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        AppTheme.styleComboBox(statusComboBox);
        statusComboBox.setSelectedItem(currentStatus == null ? "enrolled" : currentStatus.toLowerCase());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel label = new JLabel("Enrollment Status");
        label.setFont(AppTheme.bodyBoldFont(13));
        label.setForeground(AppTheme.TEXT_SECONDARY);
        add(label, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(statusComboBox, gbc);
    }

    String selectedStatus() {
        return String.valueOf(statusComboBox.getSelectedItem());
    }
}
