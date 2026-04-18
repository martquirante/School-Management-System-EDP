package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

class GradeFormPanel extends JPanel {

    private final JTextField midtermPerformanceField = new JTextField(10);
    private final JTextField midtermAttendanceField = new JTextField(10);
    private final JTextField midtermWrittenWorksField = new JTextField(10);
    private final JTextField midtermExamField = new JTextField(10);
    private final JTextField finalsPerformanceField = new JTextField(10);
    private final JTextField finalsAttendanceField = new JTextField(10);
    private final JTextField finalsWrittenWorksField = new JTextField(10);
    private final JTextField finalsExamField = new JTextField(10);

    GradeFormPanel(SchoolRepository.GradeInput gradeInput) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        styleField(midtermPerformanceField, gradeInput.midtermPerformance());
        styleField(midtermAttendanceField, gradeInput.midtermAttendance());
        styleField(midtermWrittenWorksField, gradeInput.midtermWrittenWorks());
        styleField(midtermExamField, gradeInput.midtermExam());
        styleField(finalsPerformanceField, gradeInput.finalsPerformance());
        styleField(finalsAttendanceField, gradeInput.finalsAttendance());
        styleField(finalsWrittenWorksField, gradeInput.finalsWrittenWorks());
        styleField(finalsExamField, gradeInput.finalsExam());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        addSectionLabel("Midterm Components", gbc);
        addField("Performance (40%)", midtermPerformanceField, gbc);
        addField("Attendance (10%)", midtermAttendanceField, gbc);
        addField("Written Works (20%)", midtermWrittenWorksField, gbc);
        addField("Exam (30%)", midtermExamField, gbc);

        addSectionLabel("Finals Components", gbc);
        addField("Performance (40%)", finalsPerformanceField, gbc);
        addField("Attendance (10%)", finalsAttendanceField, gbc);
        addField("Written Works (20%)", finalsWrittenWorksField, gbc);
        addField("Exam (30%)", finalsExamField, gbc);
    }

    private void styleField(JTextField field, BigDecimal value) {
        AppTheme.styleTextField(field);
        field.setText(decimalText(value));
    }

    private void addSectionLabel(String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.headingFont(15));
        label.setForeground(AppTheme.PRIMARY_ACTIVE);
        add(label, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
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

    SchoolRepository.GradeInput buildGradeInput() {
        return new SchoolRepository.GradeInput(
                parseGrade(midtermPerformanceField.getText().trim(), "Midterm Performance"),
                parseGrade(midtermAttendanceField.getText().trim(), "Midterm Attendance"),
                parseGrade(midtermWrittenWorksField.getText().trim(), "Midterm Written Works"),
                parseGrade(midtermExamField.getText().trim(), "Midterm Exam"),
                parseGrade(finalsPerformanceField.getText().trim(), "Finals Performance"),
                parseGrade(finalsAttendanceField.getText().trim(), "Finals Attendance"),
                parseGrade(finalsWrittenWorksField.getText().trim(), "Finals Written Works"),
                parseGrade(finalsExamField.getText().trim(), "Finals Exam"),
                null,
                null,
                null,
                null
        );
    }

    private BigDecimal parseGrade(String text, String label) {
        if (text.isBlank()) {
            return null;
        }
        return AcademicCalculator.parseScore(text, label);
    }

    private String decimalText(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }
}
