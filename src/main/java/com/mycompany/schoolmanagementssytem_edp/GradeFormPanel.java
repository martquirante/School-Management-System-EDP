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

    private final JTextField prelimField = new JTextField(12);
    private final JTextField midtermField = new JTextField(12);
    private final JTextField finalsField = new JTextField(12);
    private final JTextField finalGradeField = new JTextField(12);

    GradeFormPanel(BigDecimal prelim, BigDecimal midterm, BigDecimal finals, BigDecimal finalGrade) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 4, 8, 4));

        AppTheme.styleTextField(prelimField);
        AppTheme.styleTextField(midtermField);
        AppTheme.styleTextField(finalsField);
        AppTheme.styleTextField(finalGradeField);

        prelimField.setText(decimalText(prelim));
        midtermField.setText(decimalText(midterm));
        finalsField.setText(decimalText(finals));
        finalGradeField.setText(decimalText(finalGrade));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        addField("Prelim", prelimField, gbc);
        addField("Midterm", midtermField, gbc);
        addField("Finals", finalsField, gbc);
        addField("Final Grade", finalGradeField, gbc);
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
        BigDecimal prelim = parseGrade(prelimField.getText().trim(), "Prelim");
        BigDecimal midterm = parseGrade(midtermField.getText().trim(), "Midterm");
        BigDecimal finals = parseGrade(finalsField.getText().trim(), "Finals");
        BigDecimal finalGrade = parseGrade(finalGradeField.getText().trim(), "Final Grade");
        return new SchoolRepository.GradeInput(prelim, midterm, finals, finalGrade);
    }

    private BigDecimal parseGrade(String text, String label) {
        if (text.isBlank()) {
            return null;
        }
        try {
            BigDecimal grade = new BigDecimal(text);
            if (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException(label + " must be between 0 and 100.");
            }
            return grade;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be numeric.");
        }
    }

    private String decimalText(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }
}
