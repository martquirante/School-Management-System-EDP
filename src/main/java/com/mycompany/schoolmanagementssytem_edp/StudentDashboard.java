package com.mycompany.schoolmanagementssytem_edp;

public class StudentDashboard extends RoleDashboardFrame {

    public StudentDashboard(User user) {
        super(Role.STUDENT, user);
    }

    public StudentDashboard(User user, String initialSection) {
        super(Role.STUDENT, user, initialSection);
    }

    public StudentDashboard(String studentNumber) {
        this(createFallbackUser(studentNumber, "Student"));
    }

    public StudentDashboard(String studentNumber, String fullName) {
        this(createFallbackUser(studentNumber, fullName));
    }

    private static User createFallbackUser(String studentNumber, String fullName) {
        User user = new User();
        user.setUsername(studentNumber);
        user.setFullName(fullName);
        user.setRole(Role.STUDENT.getDatabaseValue());
        return user;
    }
}
