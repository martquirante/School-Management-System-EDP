package com.mycompany.schoolmanagementssytem_edp;

public class ProfessorDashboard extends RoleDashboardFrame {

    public ProfessorDashboard(User user) {
        super(Role.PROFESSOR, user);
    }

    public ProfessorDashboard(User user, String initialSection) {
        super(Role.PROFESSOR, user, initialSection);
    }
}
