package com.mycompany.schoolmanagementssytem_edp;

import javax.swing.SwingUtilities;

public class SchoolManagementSsytem_EDP {

    public static void main(String[] args) {
        DatabaseBootstrap.ensureInitialized();
        AppTheme.install();

        SwingUtilities.invokeLater(() -> {
            Admin landingPage = new Admin();
            landingPage.setVisible(true);
        });
    }
}
