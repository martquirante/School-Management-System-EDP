package com.mycompany.schoolmanagementssytem_edp;

public class StaffDashboard extends RoleDashboardFrame {

    public StaffDashboard(User user) {
        super(Role.STAFF, user);
    }

    public StaffDashboard(User user, String initialSection) {
        super(Role.STAFF, user, initialSection);
    }
}
