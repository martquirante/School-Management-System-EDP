package com.mycompany.schoolmanagementssytem_edp;

public class MetricCardData {

    private final String label;
    private final String value;
    private final String helperText;

    public MetricCardData(String label, String value, String helperText) {
        this.label = label;
        this.value = value;
        this.helperText = helperText;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getHelperText() {
        return helperText;
    }
}
