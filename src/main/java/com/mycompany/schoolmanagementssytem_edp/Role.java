package com.mycompany.schoolmanagementssytem_edp;

import java.util.Locale;

public enum Role {
    ADMIN(
            "Admin",
            "Administrator Login",
            "Admin Username",
            "admin",
            new String[]{"Dashboard", "User Accounts", "Students", "Professors", "Staff", "Reports"}
    ),
    STUDENT(
            "Student",
            "Student Login",
            "Student Number",
            "student",
            new String[]{"Dashboard", "My Profile", "My Subjects", "My Grades", "Schedule", "COR & Advising Slip"}
    ),
    PROFESSOR(
            "Professor",
            "Professor Login",
            "Professor ID",
            "professor",
            new String[]{"Dashboard", "My Profile", "My Classes", "Gradebook", "Schedule"}
    ),
    STAFF(
            "Staff",
            "Staff Login",
            "Staff Username",
            "staff",
            new String[]{"Dashboard", "My Profile", "Registrations", "Schedules", "Subjects", "Records"}
    );

    private final String displayName;
    private final String loginTitle;
    private final String usernameLabel;
    private final String databaseValue;
    private final String[] sections;

    Role(String displayName, String loginTitle, String usernameLabel, String databaseValue, String[] sections) {
        this.displayName = displayName;
        this.loginTitle = loginTitle;
        this.usernameLabel = usernameLabel;
        this.databaseValue = databaseValue;
        this.sections = sections;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLoginTitle() {
        return loginTitle;
    }

    public String getUsernameLabel() {
        return usernameLabel;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public String[] getSections() {
        return sections.clone();
    }

    public static Role fromDatabaseValue(String value) {
        if (value == null) {
            return STUDENT;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (Role role : values()) {
            if (role.databaseValue.equals(normalized)) {
                return role;
            }
        }

        return STUDENT;
    }
}
