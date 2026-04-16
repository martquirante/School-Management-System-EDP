package com.mycompany.schoolmanagementssytem_edp;

public class AdminDashboard extends RoleDashboardFrame {

    public AdminDashboard(User user) {
        super(Role.ADMIN, user);
    }

    public AdminDashboard(User user, String initialSection) {
        super(Role.ADMIN, user, initialSection);
    }
}
