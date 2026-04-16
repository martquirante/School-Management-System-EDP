package com.mycompany.schoolmanagementssytem_edp;

import java.util.List;

public class DashboardPageData {

    private final String title;
    private final String subtitle;
    private final List<MetricCardData> metrics;
    private final String tableTitle;
    private final String[] columnNames;
    private final List<Object[]> rows;
    private final String sideTitle;
    private final List<String> sideItems;

    public DashboardPageData(
            String title,
            String subtitle,
            List<MetricCardData> metrics,
            String tableTitle,
            String[] columnNames,
            List<Object[]> rows,
            String sideTitle,
            List<String> sideItems
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.metrics = metrics;
        this.tableTitle = tableTitle;
        this.columnNames = columnNames;
        this.rows = rows;
        this.sideTitle = sideTitle;
        this.sideItems = sideItems;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<MetricCardData> getMetrics() {
        return metrics;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<Object[]> getRows() {
        return rows;
    }

    public String getSideTitle() {
        return sideTitle;
    }

    public List<String> getSideItems() {
        return sideItems;
    }
}
