package com.mycompany.schoolmanagementssytem_edp;

import javax.swing.JFrame;

public final class AppNavigator {

    private AppNavigator() {
    }

    public static void openLanding(JFrame currentFrame) {
        AppTheme.install();
        show(new Admin(), currentFrame);
    }

    public static void openRoleSelection(JFrame currentFrame) {
        AppTheme.install();
        show(new SignIn(), currentFrame);
    }

    public static void openLogin(Role role, JFrame currentFrame) {
        AppTheme.install();
        JFrame loginFrame = switch (role) {
            case ADMIN -> new AdminLogIn();
            case STUDENT -> new StudentLogIn();
            case PROFESSOR -> new ProfessorLogIn();
            case STAFF -> new stafLogIn();
        };

        show(loginFrame, currentFrame);
    }

    public static void openDashboard(Role role, User user, JFrame currentFrame) {
        openDashboard(role, user, currentFrame, null);
    }

    public static void openDashboard(Role role, User user, JFrame currentFrame, String section) {
        AppTheme.install();
        JFrame dashboard = switch (role) {
            case ADMIN -> section == null ? new AdminDashboard(user) : new AdminDashboard(user, section);
            case STUDENT -> section == null ? new StudentDashboard(user) : new StudentDashboard(user, section);
            case PROFESSOR -> section == null ? new ProfessorDashboard(user) : new ProfessorDashboard(user, section);
            case STAFF -> section == null ? new StaffDashboard(user) : new StaffDashboard(user, section);
        };

        show(dashboard, currentFrame);
    }

    private static void show(JFrame nextFrame, JFrame currentFrame) {
        nextFrame.setLocationRelativeTo(currentFrame);
        nextFrame.setVisible(true);

        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }
}
