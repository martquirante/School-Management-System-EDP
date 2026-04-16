package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class ScheduleFormPanel extends JPanel {

    private final JTextField classIdField = new JTextField(16);
    private final JComboBox<String> dayComboBox = new JComboBox<>(new String[]{
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    });
    private final JTextField startTimeField = new JTextField(16);
    private final JTextField endTimeField = new JTextField(16);
    private final JTextField roomField = new JTextField(16);

    ScheduleFormPanel(Integer classId, String day, String startTime, String endTime, String room) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        AppTheme.styleTextField(classIdField);
        AppTheme.styleComboBox(dayComboBox);
        AppTheme.styleTextField(startTimeField);
        AppTheme.styleTextField(endTimeField);
        AppTheme.styleTextField(roomField);

        classIdField.setText(classId == null ? "" : String.valueOf(classId));
        if (day != null && !day.isBlank()) {
            dayComboBox.setSelectedItem(day);
        }
        startTimeField.setText(normalizeDisplayTime(startTime));
        endTimeField.setText(normalizeDisplayTime(endTime));
        roomField.setText(room == null || "-".equals(room) ? "" : room);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        addField("Class ID", classIdField, gbc);
        addField("Day", dayComboBox, gbc);
        addField("Start Time (HH:mm)", startTimeField, gbc);
        addField("End Time (HH:mm)", endTimeField, gbc);
        addField("Room", roomField, gbc);
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

    SchoolRepository.ScheduleEntry buildScheduleEntry(Integer scheduleId) {
        Integer classId;
        try {
            classId = Integer.valueOf(classIdField.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Class ID must be numeric.");
        }

        LocalTime startTime = parseTime(startTimeField.getText().trim(), "Start Time");
        LocalTime endTime = parseTime(endTimeField.getText().trim(), "End Time");
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("End Time must be later than Start Time.");
        }

        return new SchoolRepository.ScheduleEntry(
                scheduleId,
                classId,
                String.valueOf(dayComboBox.getSelectedItem()),
                startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                roomField.getText().trim().isBlank() ? null : roomField.getText().trim()
        );
    }

    private LocalTime parseTime(String text, String label) {
        try {
            return LocalTime.parse(text, DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(label + " must use HH:mm format.");
        }
    }

    private String normalizeDisplayTime(String value) {
        if (value == null || value.isBlank() || "-".equals(value)) {
            return "";
        }
        return value.length() >= 5 ? value.substring(0, 5) : value;
    }
}
