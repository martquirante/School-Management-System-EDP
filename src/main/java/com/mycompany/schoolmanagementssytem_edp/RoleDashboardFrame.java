package com.mycompany.schoolmanagementssytem_edp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class RoleDashboardFrame extends JFrame {

    private final Role role;
    private final User user;
    private final SchoolRepository repository;
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();
    private final JPanel metricsPanel = new JPanel(new GridLayout(1, 0, 16, 16));
    private final JLabel welcomeLabel = new JLabel();
    private final JLabel pageTitleLabel = new JLabel();
    private final JLabel pageSubtitleLabel = new JLabel();
    private final JLabel tableTitleLabel = new JLabel("Records");
    private final JLabel sideTitleLabel = new JLabel("Activity");
    private final JTextField searchField = new JTextField(18);
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable dataTable = new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultListModel<String> sideListModel = new DefaultListModel<>();
    private final JPanel adminToolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    private String currentSection;

    protected RoleDashboardFrame(Role role, User user) {
        this(role, user, null);
    }

    protected RoleDashboardFrame(Role role, User user, String initialSection) {
        super(role.getDisplayName() + " Dashboard");
        this.role = role;
        this.user = user;
        this.repository = new SchoolRepository();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 740));
        setSize(1380, 900);
        setLocationRelativeTo(null);

        JPanel root = AppChrome.createRootFrame(this);
        setContentPane(root);
        root.add(AppChrome.createBrandHeader(role.getDisplayName() + " dashboard workspace", () -> AppNavigator.openDashboard(role, user, this, currentSection)), BorderLayout.NORTH);

        JPanel workspace = new JPanel(new BorderLayout(18, 0));
        workspace.setOpaque(false);
        root.add(workspace, BorderLayout.CENTER);
        workspace.add(buildSidebar(), BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        workspace.add(center, BorderLayout.CENTER);

        JPanel summaryCard = AppTheme.createSectionPanel();
        summaryCard.setLayout(new BorderLayout(16, 16));
        summaryCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1), new EmptyBorder(22, 24, 22, 24)));
        center.add(summaryCard, BorderLayout.NORTH);

        JPanel summaryText = new JPanel(new BorderLayout(0, 6));
        summaryText.setOpaque(false);
        welcomeLabel.setText("Welcome, " + user.getDisplayName());
        welcomeLabel.setFont(AppTheme.displayFont(28));
        welcomeLabel.setForeground(AppTheme.TEXT_PRIMARY);
        summaryText.add(welcomeLabel, BorderLayout.NORTH);

        pageTitleLabel.setText(role.getDisplayName() + " Overview");
        pageTitleLabel.setFont(AppTheme.headingFont(20));
        pageTitleLabel.setForeground(AppTheme.PRIMARY_ACTIVE);
        summaryText.add(pageTitleLabel, BorderLayout.CENTER);

        pageSubtitleLabel.setText("Loading dashboard...");
        pageSubtitleLabel.setFont(AppTheme.bodyFont(14));
        pageSubtitleLabel.setForeground(AppTheme.TEXT_MUTED);
        summaryText.add(pageSubtitleLabel, BorderLayout.SOUTH);
        summaryCard.add(summaryText, BorderLayout.WEST);

        JPanel summaryActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        summaryActions.setOpaque(false);
        AppTheme.styleTextField(searchField);
        searchField.setToolTipText("Type to filter the current table.");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent event) {
                applyTableFilter();
            }
        });
        summaryActions.add(searchField);

        JButton refreshButton = new JButton("Refresh");
        AppTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(event -> loadSection(currentSection));
        summaryActions.add(refreshButton);
        summaryCard.add(summaryActions, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        center.add(content, BorderLayout.CENTER);
        metricsPanel.setOpaque(false);
        content.add(metricsPanel, BorderLayout.NORTH);

        dataTable.setRowHeight(34);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getTableHeader().setFont(AppTheme.bodyBoldFont(13));
        dataTable.getTableHeader().setReorderingAllowed(false);
        AppTheme.styleTable(dataTable);
        styleDataTable();
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2 && dataTable.getSelectedRow() >= 0) {
                    showSelectedRowDetails();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(AppTheme.TABLE_BACKGROUND);

        JPanel tableCard = AppTheme.createSectionPanel();
        tableCard.setLayout(new BorderLayout(0, 16));
        JPanel tableHeader = new JPanel(new BorderLayout(12, 12));
        tableHeader.setOpaque(false);
        JPanel tableHeaderText = new JPanel(new BorderLayout(0, 4));
        tableHeaderText.setOpaque(false);
        tableTitleLabel.setFont(AppTheme.headingFont(19));
        tableTitleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        tableHeaderText.add(tableTitleLabel, BorderLayout.NORTH);
        JLabel tableHintLabel = new JLabel("Double-click a row to view complete details.");
        tableHintLabel.setFont(AppTheme.bodyFont(12));
        tableHintLabel.setForeground(AppTheme.TEXT_MUTED);
        tableHeaderText.add(tableHintLabel, BorderLayout.SOUTH);
        tableHeader.add(tableHeaderText, BorderLayout.WEST);

        adminToolbarPanel.setOpaque(false);
        tableHeader.add(adminToolbarPanel, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(tableScrollPane, BorderLayout.CENTER);

        JList<String> sideList = new JList<>(sideListModel);
        sideList.setFont(AppTheme.bodyFont(14));
        sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sideList.setFixedCellHeight(28);
        sideList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(4, 10, 4, 10));
                label.setOpaque(true);
                label.setBackground(isSelected ? AppTheme.PRIMARY_SOFT : AppTheme.PANEL_BACKGROUND);
                label.setForeground(isSelected ? AppTheme.PRIMARY_ACTIVE : AppTheme.TEXT_SECONDARY);
                return label;
            }
        });        JScrollPane sideScrollPane = new JScrollPane(sideList);
        sideScrollPane.setBorder(BorderFactory.createEmptyBorder());
        sideScrollPane.getViewport().setBackground(AppTheme.PANEL_BACKGROUND);

        JPanel sideCard = AppTheme.createSectionPanel();
        sideCard.setLayout(new BorderLayout(0, 16));
        sideTitleLabel.setFont(AppTheme.headingFont(19));
        sideTitleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        sideCard.add(sideTitleLabel, BorderLayout.NORTH);
        sideCard.add(sideScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, sideCard);
        splitPane.setResizeWeight(0.72);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(10);
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false);
        content.add(splitPane, BorderLayout.CENTER);

        bindShortcuts();
        currentSection = resolveInitialSection(initialSection);
        updateAdminToolsState();
        loadSection(currentSection);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = AppTheme.createSectionPanel();
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setLayout(new BorderLayout(0, 18));
        sidebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1), new EmptyBorder(20, 18, 20, 18)));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        JLabel roleChip = new JLabel(role.getDisplayName().toUpperCase() + " PORTAL");
        roleChip.setOpaque(true);
        roleChip.setBackground(AppTheme.PRIMARY_SOFT);
        roleChip.setForeground(AppTheme.PRIMARY_ACTIVE);
        roleChip.setFont(AppTheme.bodyBoldFont(12));
        roleChip.setBorder(new EmptyBorder(10, 12, 10, 12));
        top.add(roleChip, BorderLayout.NORTH);

        JPanel userPanel = new JPanel(new BorderLayout(0, 4));
        userPanel.setOpaque(false);
        JLabel brandLabel = new JLabel("BulSU Portal");
        brandLabel.setFont(AppTheme.headingFont(22));
        brandLabel.setForeground(AppTheme.TEXT_PRIMARY);
        userPanel.add(brandLabel, BorderLayout.NORTH);
        JLabel nameLabel = new JLabel(user.getDisplayName());
        nameLabel.setFont(AppTheme.bodyFont(14));
        nameLabel.setForeground(AppTheme.TEXT_MUTED);
        userPanel.add(nameLabel, BorderLayout.SOUTH);
        top.add(userPanel, BorderLayout.CENTER);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        navPanel.setOpaque(false);
        for (String section : role.getSections()) {
            JButton button = new JButton(section);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            AppTheme.styleNavigationButton(button);
            button.addActionListener(event -> loadSection(section));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent event) {
                    if (!section.equals(currentSection)) {
                        button.setBackground(AppTheme.PRIMARY_SOFT);
                        button.setForeground(AppTheme.PRIMARY_ACTIVE);
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent event) {
                    if (!section.equals(currentSection)) {
                        AppTheme.setNavigationActive(button, false);
                    }
                }
            });
            navigationButtons.put(section, button);
            navPanel.add(button);
        }
        sidebar.add(navPanel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        AppTheme.styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(event -> logout());
        JPanel footer = new JPanel(new BorderLayout(0, 10));
        footer.setOpaque(false);
        JLabel helpLabel = new JLabel("Esc logs out. Ctrl+R reloads this section.");
        helpLabel.setFont(AppTheme.bodyFont(12));
        helpLabel.setForeground(AppTheme.TEXT_MUTED);
        footer.add(helpLabel, BorderLayout.NORTH);
        footer.add(logoutButton, BorderLayout.SOUTH);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void styleDataTable() {
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                label.setForeground(isSelected ? AppTheme.TABLE_SELECTION_TEXT : AppTheme.TEXT_PRIMARY);
                label.setBackground(isSelected ? AppTheme.TABLE_SELECTION : (row % 2 == 0 ? AppTheme.TABLE_BACKGROUND : AppTheme.TABLE_ALT_BACKGROUND));
                return label;
            }
        });
    }

    private void bindShortcuts() {
        getRootPane().registerKeyboardAction(event -> loadSection(currentSection), KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), JPanel.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(event -> { if (isAdminAccountsSection()) { openCreateAccountDialog(); } }, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), JPanel.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(event -> { if (isAdminAccountsSection()) { openEditAccountDialog(); } }, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), JPanel.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(event -> { if (isAdminAccountsSection()) { openDeleteAccountDialog(); } }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JPanel.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(event -> logout(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JPanel.WHEN_IN_FOCUSED_WINDOW);
    }

    private String resolveInitialSection(String initialSection) {
        if (initialSection == null || initialSection.isBlank()) {
            return role.getSections()[0];
        }
        for (String section : role.getSections()) {
            if (section.equalsIgnoreCase(initialSection)) {
                return section;
            }
        }
        return role.getSections()[0];
    }

    private void loadSection(String section) {
        currentSection = section;
        updateNavigationState();
        updateAdminToolsState();
        pageTitleLabel.setText(section);
        pageSubtitleLabel.setText("Loading " + section.toLowerCase() + "...");

        new SwingWorker<DashboardPageData, Void>() {
            @Override
            protected DashboardPageData doInBackground() {
                return repository.loadPage(role, user, section);
            }

            @Override
            protected void done() {
                try {
                    applyPageData(get());
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(RoleDashboardFrame.this, "Unable to load dashboard data.\n\n" + exception.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void applyPageData(DashboardPageData pageData) {
        pageTitleLabel.setText(pageData.getTitle());
        pageSubtitleLabel.setText(pageData.getSubtitle());
        tableTitleLabel.setText(pageData.getTableTitle());
        sideTitleLabel.setText(pageData.getSideTitle());
        welcomeLabel.setText("Welcome, " + user.getDisplayName());

        metricsPanel.removeAll();
        for (MetricCardData metric : pageData.getMetrics()) {
            metricsPanel.add(createMetricCard(metric));
        }
        metricsPanel.revalidate();
        metricsPanel.repaint();

        tableModel.setDataVector(pageData.getRows().toArray(Object[][]::new), pageData.getColumnNames());
        dataTable.setRowSorter(new TableRowSorter<>(tableModel));
        applyTableFilter();

        sideListModel.clear();
        for (String item : pageData.getSideItems()) {
            sideListModel.addElement("- " + item);
        }
        updateAdminToolsState();
    }

    private JPanel createMetricCard(MetricCardData metric) {
        JPanel card = AppTheme.createSectionPanel();
        card.setLayout(new BorderLayout(0, 8));
        JLabel label = new JLabel(metric.getLabel());
        label.setFont(AppTheme.bodyBoldFont(13));
        label.setForeground(AppTheme.TEXT_MUTED);
        card.add(label, BorderLayout.NORTH);
        JLabel value = new JLabel(metric.getValue());
        value.setFont(AppTheme.displayFont(30));
        value.setForeground(AppTheme.PRIMARY_ACTIVE);
        card.add(value, BorderLayout.CENTER);
        JLabel helper = new JLabel("<html><div style='width:180px'>" + metric.getHelperText() + "</div></html>");
        helper.setFont(AppTheme.bodyFont(12));
        helper.setForeground(AppTheme.TEXT_SECONDARY);
        card.add(helper, BorderLayout.SOUTH);
        return card;
    }

    private void applyTableFilter() {
        if (!(dataTable.getRowSorter() instanceof TableRowSorter<?> sorter)) {
            return;
        }
        String searchText = searchField.getText().trim();
        sorter.setRowFilter(searchText.isBlank() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(searchText)));
    }

    private void showSelectedRowDetails() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        int modelRow = dataTable.convertRowIndexToModel(selectedRow);
        StringBuilder details = new StringBuilder();
        for (int column = 0; column < tableModel.getColumnCount(); column++) {
            details.append(tableModel.getColumnName(column)).append(": ").append(tableModel.getValueAt(modelRow, column)).append('\n');
        }
        JOptionPane.showMessageDialog(this, details.toString(), tableTitleLabel.getText(), JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isAdminAccountsSection() {
        return role == Role.ADMIN && "User Accounts".equalsIgnoreCase(currentSection);
    }

    private void updateAdminToolsState() {
        adminToolbarPanel.removeAll();

        if (isAdminAccountsSection()) {
            addPrimaryToolbarButton("Add Account", this::openCreateAccountDialog);
            addSecondaryToolbarButton("Edit Selected", this::openEditAccountDialog);
            addDangerToolbarButton("Delete", this::openDeleteAccountDialog);
        } else if (role == Role.PROFESSOR && "My Classes".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("View Master List", this::openProfessorMasterListDialog);
            addSecondaryToolbarButton("Export Excel", this::exportProfessorGradeTemplate);
            addSecondaryToolbarButton("Import Excel", this::importProfessorGradeTemplate);
        } else if (role == Role.PROFESSOR && "Gradebook".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Post Grade", this::openGradeDialog);
        } else if (role == Role.STAFF && "Registrations".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Update Status", this::openEnrollmentStatusDialog);
        } else if (role == Role.STAFF && "Schedules".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Add Schedule", this::openCreateScheduleDialog);
            addSecondaryToolbarButton("Edit Selected", this::openEditScheduleDialog);
            addDangerToolbarButton("Delete", this::openDeleteScheduleDialog);
        } else if (role == Role.STAFF && "Subjects".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Add Subject", this::openCreateSubjectDialog);
            addSecondaryToolbarButton("Edit Selected", this::openEditSubjectDialog);
            addDangerToolbarButton("Delete", this::openDeleteSubjectDialog);
        } else if (role == Role.STUDENT && "My Grades".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Download PDF", this::exportStudentGradesPdf);
        } else if (role == Role.STUDENT && "COR & Advising Slip".equalsIgnoreCase(currentSection)) {
            addPrimaryToolbarButton("Download PDF", this::exportStudentCorPdf);
            addPrimaryToolbarButton("Download HTML", this::exportStudentDocument);
        }

        adminToolbarPanel.setVisible(adminToolbarPanel.getComponentCount() > 0);
        adminToolbarPanel.revalidate();
        adminToolbarPanel.repaint();
    }

    private void addPrimaryToolbarButton(String label, Runnable action) {
        JButton button = new JButton(label);
        AppTheme.stylePrimaryButton(button);
        button.addActionListener(event -> action.run());
        button.addMouseListener(AppTheme.clickPulse(button));
        adminToolbarPanel.add(button);
    }

    private void addSecondaryToolbarButton(String label, Runnable action) {
        JButton button = new JButton(label);
        AppTheme.styleSecondaryButton(button);
        button.addActionListener(event -> action.run());
        adminToolbarPanel.add(button);
    }

    private void addDangerToolbarButton(String label, Runnable action) {
        JButton button = new JButton(label);
        AppTheme.styleDangerButton(button);
        button.addActionListener(event -> action.run());
        adminToolbarPanel.add(button);
    }

    private void openCreateAccountDialog() {
        AccountFormPanel formPanel = new AccountFormPanel(null);
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Add New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.createUser(formPanel.buildUser());
                JOptionPane.showMessageDialog(this, "Account created successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to create the account.\n\n" + exception.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openEditAccountDialog() {
        Integer userId = selectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Select a valid account row from the table first.", "No Account Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User selectedUser = repository.findUserById(userId);
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "The selected user could not be found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                loadSection(currentSection);
                return;
            }

            AccountFormPanel formPanel = new AccountFormPanel(selectedUser);
            while (true) {
                int option = JOptionPane.showConfirmDialog(this, formPanel, "Edit Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }
                try {
                    User updatedUser = formPanel.buildUser();
                    updatedUser.setUserId(userId);
                    repository.updateUser(updatedUser);
                    JOptionPane.showMessageDialog(this, "Account updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                    loadSection(currentSection);
                    return;
                } catch (IllegalArgumentException exception) {
                    JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to load the selected account.\n\n" + exception.getMessage(), "Edit Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDeleteAccountDialog() {
        Integer userId = selectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Select a valid account row from the table first.", "No Account Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, "Delete the selected account from the users table?", "Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            repository.deleteUser(userId);
            JOptionPane.showMessageDialog(this, "Account deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadSection(currentSection);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to delete the selected account.\n\n" + exception.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openGradeDialog() {
        Integer enrollmentId = selectedInteger("Enrollment Id");
        if (enrollmentId == null) {
            JOptionPane.showMessageDialog(this, "Select a gradebook row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GradeFormPanel formPanel;
        try {
            formPanel = new GradeFormPanel(repository.findGradeInput(enrollmentId));
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to load the selected grade record.\n\n" + exception.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Post Grade", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.saveGrade(enrollmentId, formPanel.buildGradeInput());
                JOptionPane.showMessageDialog(this, "Grades saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to save the grade.\n\n" + exception.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openEnrollmentStatusDialog() {
        Integer enrollmentId = selectedInteger("Enrollment Id");
        if (enrollmentId == null) {
            JOptionPane.showMessageDialog(this, "Select a registration row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EnrollmentStatusFormPanel formPanel = new EnrollmentStatusFormPanel(stringValue(selectedValue("Status"), "enrolled"));
        int option = JOptionPane.showConfirmDialog(this, formPanel, "Update Enrollment Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            repository.updateEnrollmentStatus(enrollmentId, formPanel.selectedStatus());
            JOptionPane.showMessageDialog(this, "Enrollment status updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
            loadSection(currentSection);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to update the registration.\n\n" + exception.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCreateScheduleDialog() {
        ScheduleFormPanel formPanel = new ScheduleFormPanel(null, null, null, null, null);
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Add Schedule", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.createSchedule(formPanel.buildScheduleEntry(null));
                JOptionPane.showMessageDialog(this, "Schedule created successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to create the schedule.\n\n" + exception.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openEditScheduleDialog() {
        Integer scheduleId = selectedInteger("Schedule Id");
        if (scheduleId == null) {
            JOptionPane.showMessageDialog(this, "Select a schedule row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] timeRange = splitTimeRange(stringValue(selectedValue("Time"), ""));
        ScheduleFormPanel formPanel = new ScheduleFormPanel(
                selectedInteger("Class Id"),
                stringValue(selectedValue("Day"), null),
                timeRange[0],
                timeRange[1],
                stringValue(selectedValue("Room"), null)
        );

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Edit Schedule", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.updateSchedule(formPanel.buildScheduleEntry(scheduleId));
                JOptionPane.showMessageDialog(this, "Schedule updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to update the schedule.\n\n" + exception.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openDeleteScheduleDialog() {
        Integer scheduleId = selectedInteger("Schedule Id");
        if (scheduleId == null) {
            JOptionPane.showMessageDialog(this, "Select a schedule row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Delete the selected schedule record?", "Delete Schedule", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            repository.deleteSchedule(scheduleId);
            JOptionPane.showMessageDialog(this, "Schedule deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadSection(currentSection);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to delete the schedule.\n\n" + exception.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCreateSubjectDialog() {
        SubjectFormPanel formPanel = new SubjectFormPanel(null, null, null, null, "active");
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Add Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.createSubject(formPanel.buildSubjectEntry(null));
                JOptionPane.showMessageDialog(this, "Subject created successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to create the subject.\n\n" + exception.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openEditSubjectDialog() {
        Integer subjectId = selectedInteger("Subject Id");
        if (subjectId == null) {
            JOptionPane.showMessageDialog(this, "Select a subject row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SubjectFormPanel formPanel = new SubjectFormPanel(
                stringValue(selectedValue("Code"), null),
                stringValue(selectedValue("Subject"), null),
                parseDecimal(selectedValue("Units")),
                selectedInteger("Department Id"),
                stringValue(selectedValue("Status"), "active")
        );

        while (true) {
            int option = JOptionPane.showConfirmDialog(this, formPanel, "Edit Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                repository.updateSubject(formPanel.buildSubjectEntry(subjectId));
                JOptionPane.showMessageDialog(this, "Subject updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                loadSection(currentSection);
                return;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Unable to update the subject.\n\n" + exception.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void openDeleteSubjectDialog() {
        Integer subjectId = selectedInteger("Subject Id");
        if (subjectId == null) {
            JOptionPane.showMessageDialog(this, "Select a subject row first.", "No Record Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Delete the selected subject record?", "Delete Subject", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            repository.deleteSubject(subjectId);
            JOptionPane.showMessageDialog(this, "Subject deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadSection(currentSection);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to delete the subject.\n\n" + exception.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportStudentDocument() {
        try {
            String html = repository.buildStudentDocumentHtml(user);
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save COR / Advising Slip");
            chooser.setFileFilter(new FileNameExtensionFilter("HTML files", "html"));
            chooser.setSelectedFile(new java.io.File(user.getDisplayName().replaceAll("\\s+", "_") + "_COR.html"));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Path outputPath = chooser.getSelectedFile().toPath();
            if (!outputPath.toString().toLowerCase().endsWith(".html")) {
                outputPath = Path.of(outputPath.toString() + ".html");
            }

            Files.writeString(outputPath, html, StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, "Document saved to:\n" + outputPath, "Download Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to export the document.\n\n" + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportStudentCorPdf() {
        exportStudentPdf("Save COR / Advising Slip PDF", user.getDisplayName().replaceAll("\\s+", "_") + "_COR.pdf", true);
    }

    private void exportStudentGradesPdf() {
        exportStudentPdf("Save Report of Grades PDF", user.getDisplayName().replaceAll("\\s+", "_") + "_ROG.pdf", false);
    }

    private void exportStudentPdf(String dialogTitle, String suggestedFileName, boolean corDocument) {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(dialogTitle);
            chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
            chooser.setSelectedFile(new java.io.File(suggestedFileName));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Path outputPath = chooser.getSelectedFile().toPath();
            if (!outputPath.toString().toLowerCase().endsWith(".pdf")) {
                outputPath = Path.of(outputPath.toString() + ".pdf");
            }

            if (corDocument) {
                repository.exportStudentCorPdf(user, outputPath);
            } else {
                repository.exportStudentGradesPdf(user, outputPath);
            }

            JOptionPane.showMessageDialog(this, "PDF saved to:\n" + outputPath, "Download Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to export the PDF.\n\n" + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProfessorMasterListDialog() {
        Integer classId = selectedInteger("Class Id");
        if (classId == null) {
            JOptionPane.showMessageDialog(this, "Select a class row first.", "No Class Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SchoolRepository.TableData masterList = repository.loadProfessorMasterList(user, classId);
            DefaultTableModel model = new DefaultTableModel(masterList.rows().toArray(Object[][]::new), masterList.columns()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(model);
            table.setRowHeight(30);
            AppTheme.styleTable(table);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(980, 360));

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setOpaque(false);
            JLabel title = new JLabel("Class Master List for Class " + classId);
            title.setFont(AppTheme.headingFont(18));
            title.setForeground(AppTheme.TEXT_PRIMARY);
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, panel, "Master List", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to load the selected class list.\n\n" + exception.getMessage(), "Master List Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportProfessorGradeTemplate() {
        Integer classId = selectedInteger("Class Id");
        if (classId == null) {
            JOptionPane.showMessageDialog(this, "Select a class row first.", "No Class Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export Grade Template");
            chooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx"));
            chooser.setSelectedFile(new java.io.File("class_" + classId + "_grade_template.xlsx"));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Path outputPath = chooser.getSelectedFile().toPath();
            if (!outputPath.toString().toLowerCase().endsWith(".xlsx")) {
                outputPath = Path.of(outputPath.toString() + ".xlsx");
            }

            repository.exportProfessorGradeTemplate(user, classId, outputPath);
            JOptionPane.showMessageDialog(this, "Excel template saved to:\n" + outputPath, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to export the Excel template.\n\n" + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importProfessorGradeTemplate() {
        Integer classId = selectedInteger("Class Id");
        if (classId == null) {
            JOptionPane.showMessageDialog(this, "Select a class row first.", "No Class Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Import Encoded Grades");
            chooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx"));

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            int importedCount = repository.importProfessorGradeTemplate(user, classId, chooser.getSelectedFile().toPath());
            JOptionPane.showMessageDialog(this, importedCount + " grade row(s) imported and computed successfully.", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
            loadSection("Gradebook");
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to import the Excel file.\n\n" + exception.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer selectedUserId() {
        return selectedInteger("User Id");
    }

    private Integer selectedInteger(String... columnNames) {
        Object value = selectedValue(columnNames);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Object selectedValue(String... columnNames) {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        int modelRow = dataTable.convertRowIndexToModel(selectedRow);
        for (String columnName : columnNames) {
            int columnIndex = findColumnIndex(columnName);
            if (columnIndex >= 0) {
                return tableModel.getValueAt(modelRow, columnIndex);
            }
        }
        return null;
    }

    private String stringValue(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String text = String.valueOf(value).trim();
        return text.isBlank() || "-".equals(text) ? fallback : text;
    }

    private BigDecimal parseDecimal(Object value) {
        String text = stringValue(value, null);
        if (text == null) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String[] splitTimeRange(String timeRange) {
        String[] parts = timeRange.split("\\s+-\\s+");
        if (parts.length == 2) {
            return parts;
        }
        return new String[]{null, null};
    }

    private int findColumnIndex(String columnName) {
        for (int column = 0; column < tableModel.getColumnCount(); column++) {
            if (columnName.equalsIgnoreCase(tableModel.getColumnName(column))) {
                return column;
            }
        }
        return -1;
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "Log out and return to role selection?", "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            AppNavigator.openRoleSelection(this);
        }
    }

    private void updateNavigationState() {
        navigationButtons.forEach((section, button) -> AppTheme.setNavigationActive(button, section.equals(currentSection)));
    }
}
