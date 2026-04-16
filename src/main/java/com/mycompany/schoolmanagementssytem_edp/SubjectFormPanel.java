package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class SubjectFormPanel extends JPanel {

    private final JTextField codeField = new JTextField(18);
    private final JTextField nameField = new JTextField(18);
    private final JTextField unitsField = new JTextField(18);
    private final JComboBox<DepartmentItem> departmentComboBox = new JComboBox<>();
    private final JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"active", "inactive"});

    SubjectFormPanel(String code, String name, BigDecimal units, Integer departmentId, String status) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        AppTheme.styleTextField(codeField);
        AppTheme.styleTextField(nameField);
        AppTheme.styleTextField(unitsField);
        AppTheme.styleComboBox(departmentComboBox);
        AppTheme.styleComboBox(statusComboBox);

        codeField.setText(code == null ? "" : code);
        nameField.setText(name == null ? "" : name);
        unitsField.setText(units == null ? "" : units.stripTrailingZeros().toPlainString());
        statusComboBox.setSelectedItem(status == null ? "active" : status.toLowerCase());

        loadDepartments();

        if (departmentId != null) {
            selectDepartmentById(departmentId);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        addField("Subject Code", codeField, gbc);
        addField("Subject Name", nameField, gbc);
        addField("Units", unitsField, gbc);
        addField("Department", departmentComboBox, gbc);
        addField("Status", statusComboBox, gbc);
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

    private void loadDepartments() {
        departmentComboBox.removeAllItems();

        String sql = "SELECT department_id, department_name FROM departments ORDER BY department_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                departmentComboBox.addItem(new DepartmentItem(
                        rs.getInt("department_id"),
                        rs.getString("department_name")
                ));
            }

            if (departmentComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No departments found. Please add a department first.",
                        "No Departments",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load departments: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void selectDepartmentById(Integer departmentId) {
        for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
            DepartmentItem item = departmentComboBox.getItemAt(i);
            if (item != null && item.getId().equals(departmentId)) {
                departmentComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    SchoolRepository.SubjectEntry buildSubjectEntry(Integer subjectId) {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String unitsText = unitsField.getText().trim();

        if (code.isBlank()) {
            throw new IllegalArgumentException("Subject code is required.");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Subject name is required.");
        }

        if (unitsText.isBlank()) {
            throw new IllegalArgumentException("Units are required.");
        }

        BigDecimal units;
        try {
            units = new BigDecimal(unitsText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Units must be numeric.");
        }

        if (units.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Units must be greater than zero.");
        }

        DepartmentItem selectedDepartment = (DepartmentItem) departmentComboBox.getSelectedItem();
        if (selectedDepartment == null) {
            throw new IllegalArgumentException("Please select a department.");
        }

        Integer departmentId = selectedDepartment.getId();
        String status = String.valueOf(statusComboBox.getSelectedItem());

        return new SchoolRepository.SubjectEntry(
                subjectId,
                code,
                name,
                units,
                departmentId,
                status
        );
    }

    static class DepartmentItem {
        private final Integer id;
        private final String name;

        DepartmentItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}