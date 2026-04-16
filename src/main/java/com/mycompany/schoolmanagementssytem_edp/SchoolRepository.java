package com.mycompany.schoolmanagementssytem_edp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SchoolRepository {

    public record GradeInput(BigDecimal prelim, BigDecimal midterm, BigDecimal finals, BigDecimal finalGrade) {

        public BigDecimal resolvedFinalGrade() {
            if (finalGrade != null) {
                return finalGrade;
            }

            BigDecimal total = BigDecimal.ZERO;
            int count = 0;

            if (prelim != null) {
                total = total.add(prelim);
                count++;
            }
            if (midterm != null) {
                total = total.add(midterm);
                count++;
            }
            if (finals != null) {
                total = total.add(finals);
                count++;
            }

            return count == 0 ? null : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        }
    }

    public record ScheduleEntry(
            Integer scheduleId,
            Integer classId,
            String dayOfWeek,
            String startTime,
            String endTime,
            String room
    ) {
    }

    public record SubjectEntry(
            Integer subjectId,
            String subjectCode,
            String subjectName,
            BigDecimal units,
            Integer departmentId,
            String status
    ) {
    }

    public User authenticate(Role role, String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND LOWER(role) = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role.getDatabaseValue());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                User user = mapUser(resultSet);
                if (user.getFullName() == null || user.getFullName().isBlank()) {
                    user.setFullName(resolveFullName(connection, role, user));
                }
                return user;
            }
        }
    }

    public boolean resetPassword(Role role, String username, String email, String newPassword) throws SQLException {
        String verifySql = "SELECT user_id FROM users WHERE username = ? AND email = ? AND LOWER(role) = ?";
        String updateSql = "UPDATE users SET password = ? WHERE username = ? AND email = ? AND LOWER(role) = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement verifyStatement = connection.prepareStatement(verifySql)) {
            verifyStatement.setString(1, username);
            verifyStatement.setString(2, email);
            verifyStatement.setString(3, role.getDatabaseValue());

            try (ResultSet resultSet = verifyStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                }
            }

            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                updateStatement.setString(1, newPassword);
                updateStatement.setString(2, username);
                updateStatement.setString(3, email);
                updateStatement.setString(4, role.getDatabaseValue());
                return updateStatement.executeUpdate() > 0;
            }
        }
    }

    public User findUserById(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String usersTable = firstExistingTable(connection, "users");
            if (usersTable == null) {
                throw new SQLException("The users table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, usersTable);
            if (!columns.contains("user_id")) {
                throw new SQLException("The users table does not contain a user_id column.");
            }

            String sql = "SELECT * FROM " + usersTable + " WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }

                    User user = mapUser(resultSet);
                    if (user.getFullName() == null || user.getFullName().isBlank()) {
                        Role role = Role.fromDatabaseValue(user.getRole());
                        user.setFullName(resolveFullName(connection, role, user));
                    }
                    return user;
                }
            }
        }
    }

    public void createUser(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String usersTable = firstExistingTable(connection, "users");
            if (usersTable == null) {
                throw new SQLException("The users table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, usersTable);
            String displayNameColumn = firstAvailableColumn(columns, "full_name", "display_name", "name");
            String roleValue = normalizeRoleValue(user.getRole());

            List<String> insertColumns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            addInsertValue(columns, insertColumns, values, "username", user.getUsername());
            addInsertValue(columns, insertColumns, values, "email", user.getEmail());
            addInsertValue(columns, insertColumns, values, "password", user.getPassword());
            addInsertValue(columns, insertColumns, values, "role", roleValue);
            addInsertValue(columns, insertColumns, values, displayNameColumn, user.getFullName());

            Role selectedRole = Role.fromDatabaseValue(roleValue);
            addInsertValue(columns, insertColumns, values, "student_id", selectedRole == Role.STUDENT ? user.getStudentId() : null);
            addInsertValue(columns, insertColumns, values, "professor_id", selectedRole == Role.PROFESSOR ? user.getProfessorId() : null);
            addInsertValue(columns, insertColumns, values, "admin_id", selectedRole == Role.ADMIN ? user.getAdminId() : null);
            addInsertValue(columns, insertColumns, values, "staff_id", selectedRole == Role.STAFF ? user.getStaffId() : null);

            if (insertColumns.isEmpty()) {
                throw new SQLException("No supported users table columns were found for insert.");
            }

            String placeholders = String.join(", ", insertColumns.stream().map(column -> "?").toList());
            String sql = "INSERT INTO " + usersTable + " (" + String.join(", ", insertColumns) + ") VALUES (" + placeholders + ")";

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindParameters(statement, values);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setUserId(keys.getInt(1));
                    }
                }
            }
        }
    }

    public void updateUser(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String usersTable = firstExistingTable(connection, "users");
            if (usersTable == null) {
                throw new SQLException("The users table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, usersTable);
            if (!columns.contains("user_id")) {
                throw new SQLException("The users table does not contain a user_id column.");
            }

            String displayNameColumn = firstAvailableColumn(columns, "full_name", "display_name", "name");
            String roleValue = normalizeRoleValue(user.getRole());
            Role selectedRole = Role.fromDatabaseValue(roleValue);

            List<String> assignments = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            addUpdateValue(columns, assignments, values, "username", user.getUsername());
            addUpdateValue(columns, assignments, values, "email", user.getEmail());
            addUpdateValue(columns, assignments, values, "password", user.getPassword());
            addUpdateValue(columns, assignments, values, "role", roleValue);
            addUpdateValue(columns, assignments, values, displayNameColumn, user.getFullName());
            addUpdateValue(columns, assignments, values, "student_id", selectedRole == Role.STUDENT ? user.getStudentId() : null);
            addUpdateValue(columns, assignments, values, "professor_id", selectedRole == Role.PROFESSOR ? user.getProfessorId() : null);
            addUpdateValue(columns, assignments, values, "admin_id", selectedRole == Role.ADMIN ? user.getAdminId() : null);
            addUpdateValue(columns, assignments, values, "staff_id", selectedRole == Role.STAFF ? user.getStaffId() : null);

            if (assignments.isEmpty()) {
                throw new SQLException("No supported users table columns were found for update.");
            }

            values.add(user.getUserId());
            String sql = "UPDATE " + usersTable + " SET " + String.join(", ", assignments) + " WHERE user_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, values);
                statement.executeUpdate();
            }
        }
    }

    public void deleteUser(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String usersTable = firstExistingTable(connection, "users");
            if (usersTable == null) {
                throw new SQLException("The users table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, usersTable);
            if (!columns.contains("user_id")) {
                throw new SQLException("The users table does not contain a user_id column.");
            }

            String sql = "DELETE FROM " + usersTable + " WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }
        }
    }

    public void saveGrade(int enrollmentId, GradeInput gradeInput) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String gradesTable = firstExistingTable(connection, "grades");
            if (gradesTable == null) {
                throw new SQLException("The grades table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, gradesTable);
            if (!columns.contains("enrollment_id")) {
                throw new SQLException("The grades table does not contain an enrollment_id column.");
            }

            boolean exists;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT enrollment_id FROM " + gradesTable + " WHERE enrollment_id = ?")) {
                statement.setInt(1, enrollmentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    exists = resultSet.next();
                }
            }

            List<String> assignments = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            addUpdateValue(columns, assignments, values, "prelim", gradeInput.prelim());
            addUpdateValue(columns, assignments, values, "midterm", gradeInput.midterm());
            addUpdateValue(columns, assignments, values, "finals", gradeInput.finals());
            addUpdateValue(columns, assignments, values, "final_grade", gradeInput.resolvedFinalGrade());
            addUpdateValue(columns, assignments, values, "grade_value", gradeInput.resolvedFinalGrade());

            if (assignments.isEmpty()) {
                throw new SQLException("No supported grade columns were found for update.");
            }

            if (exists) {
                values.add(enrollmentId);
                String sql = "UPDATE " + gradesTable + " SET " + String.join(", ", assignments) + " WHERE enrollment_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    bindParameters(statement, values);
                    statement.executeUpdate();
                }
                return;
            }

            List<String> insertColumns = new ArrayList<>();
            List<Object> insertValues = new ArrayList<>();
            addInsertValue(columns, insertColumns, insertValues, "enrollment_id", enrollmentId);
            addInsertValue(columns, insertColumns, insertValues, "prelim", gradeInput.prelim());
            addInsertValue(columns, insertColumns, insertValues, "midterm", gradeInput.midterm());
            addInsertValue(columns, insertColumns, insertValues, "finals", gradeInput.finals());
            addInsertValue(columns, insertColumns, insertValues, "final_grade", gradeInput.resolvedFinalGrade());
            addInsertValue(columns, insertColumns, insertValues, "grade_value", gradeInput.resolvedFinalGrade());

            String placeholders = String.join(", ", insertColumns.stream().map(column -> "?").toList());
            String sql = "INSERT INTO " + gradesTable + " (" + String.join(", ", insertColumns) + ") VALUES (" + placeholders + ")";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, insertValues);
                statement.executeUpdate();
            }
        }
    }

    public void updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String enrollmentsTable = firstExistingTable(connection, "enrollments");
            if (enrollmentsTable == null) {
                throw new SQLException("The enrollments table was not found in the current database.");
            }

            String sql = "UPDATE " + enrollmentsTable + " SET status = ? WHERE enrollment_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, status);
                statement.setInt(2, enrollmentId);
                statement.executeUpdate();
            }
        }
    }

    public void createSchedule(ScheduleEntry scheduleEntry) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String scheduleTable = firstExistingTable(connection, "class_schedules");
            if (scheduleTable == null) {
                throw new SQLException("The class_schedules table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, scheduleTable);
            List<String> insertColumns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            addInsertValue(columns, insertColumns, values, "class_id", scheduleEntry.classId());
            addInsertValue(columns, insertColumns, values, "day_of_week", scheduleEntry.dayOfWeek());
            addInsertValue(columns, insertColumns, values, "start_time", scheduleEntry.startTime());
            addInsertValue(columns, insertColumns, values, "end_time", scheduleEntry.endTime());
            addInsertValue(columns, insertColumns, values, "room", scheduleEntry.room());

            if (insertColumns.isEmpty()) {
                throw new SQLException("No supported schedule columns were found for insert.");
            }

            String placeholders = String.join(", ", insertColumns.stream().map(column -> "?").toList());
            String sql = "INSERT INTO " + scheduleTable + " (" + String.join(", ", insertColumns) + ") VALUES (" + placeholders + ")";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, values);
                statement.executeUpdate();
            }
        }
    }

    public void updateSchedule(ScheduleEntry scheduleEntry) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String scheduleTable = firstExistingTable(connection, "class_schedules");
            if (scheduleTable == null) {
                throw new SQLException("The class_schedules table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, scheduleTable);
            if (!columns.contains("class_schedule_id")) {
                throw new SQLException("The class_schedules table does not contain a class_schedule_id column.");
            }

            List<String> assignments = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            addUpdateValue(columns, assignments, values, "class_id", scheduleEntry.classId());
            addUpdateValue(columns, assignments, values, "day_of_week", scheduleEntry.dayOfWeek());
            addUpdateValue(columns, assignments, values, "start_time", scheduleEntry.startTime());
            addUpdateValue(columns, assignments, values, "end_time", scheduleEntry.endTime());
            addUpdateValue(columns, assignments, values, "room", scheduleEntry.room());

            if (assignments.isEmpty()) {
                throw new SQLException("No supported schedule columns were found for update.");
            }

            values.add(scheduleEntry.scheduleId());
            String sql = "UPDATE " + scheduleTable + " SET " + String.join(", ", assignments) + " WHERE class_schedule_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, values);
                statement.executeUpdate();
            }
        }
    }

    public void deleteSchedule(int scheduleId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String scheduleTable = firstExistingTable(connection, "class_schedules");
            if (scheduleTable == null) {
                throw new SQLException("The class_schedules table was not found in the current database.");
            }

            String sql = "DELETE FROM " + scheduleTable + " WHERE class_schedule_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, scheduleId);
                statement.executeUpdate();
            }
        }
    }

    public void createSubject(SubjectEntry subjectEntry) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String subjectsTable = firstExistingTable(connection, "subjects");
            if (subjectsTable == null) {
                throw new SQLException("The subjects table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, subjectsTable);
            List<String> insertColumns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            addInsertValue(columns, insertColumns, values, "subject_code", subjectEntry.subjectCode());
            addInsertValue(columns, insertColumns, values, "subject_name", subjectEntry.subjectName());
            addInsertValue(columns, insertColumns, values, "units", subjectEntry.units());
            addInsertValue(columns, insertColumns, values, "department_id", subjectEntry.departmentId());
            addInsertValue(columns, insertColumns, values, "status", subjectEntry.status());

            if (insertColumns.isEmpty()) {
                throw new SQLException("No supported subject columns were found for insert.");
            }

            String placeholders = String.join(", ", insertColumns.stream().map(column -> "?").toList());
            String sql = "INSERT INTO " + subjectsTable + " (" + String.join(", ", insertColumns) + ") VALUES (" + placeholders + ")";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, values);
                statement.executeUpdate();
            }
        }
    }

    public void updateSubject(SubjectEntry subjectEntry) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String subjectsTable = firstExistingTable(connection, "subjects");
            if (subjectsTable == null) {
                throw new SQLException("The subjects table was not found in the current database.");
            }

            Set<String> columns = tableColumns(connection, subjectsTable);
            if (!columns.contains("subject_id")) {
                throw new SQLException("The subjects table does not contain a subject_id column.");
            }

            List<String> assignments = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            addUpdateValue(columns, assignments, values, "subject_code", subjectEntry.subjectCode());
            addUpdateValue(columns, assignments, values, "subject_name", subjectEntry.subjectName());
            addUpdateValue(columns, assignments, values, "units", subjectEntry.units());
            addUpdateValue(columns, assignments, values, "department_id", subjectEntry.departmentId());
            addUpdateValue(columns, assignments, values, "status", subjectEntry.status());

            if (assignments.isEmpty()) {
                throw new SQLException("No supported subject columns were found for update.");
            }

            values.add(subjectEntry.subjectId());
            String sql = "UPDATE " + subjectsTable + " SET " + String.join(", ", assignments) + " WHERE subject_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindParameters(statement, values);
                statement.executeUpdate();
            }
        }
    }

    public void deleteSubject(int subjectId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            String subjectsTable = firstExistingTable(connection, "subjects");
            if (subjectsTable == null) {
                throw new SQLException("The subjects table was not found in the current database.");
            }

            String sql = "DELETE FROM " + subjectsTable + " WHERE subject_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, subjectId);
                statement.executeUpdate();
            }
        }
    }

    public String buildStudentDocumentHtml(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            TableData profile = profileTable(connection, Role.STUDENT, user);
            TableData documentTable = loadStudentDocumentRows(connection, user);

            StringBuilder builder = new StringBuilder();
            builder.append("""
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Certificate of Registration</title>
                        <style>
                            body { font-family: Arial, sans-serif; color: #1f2937; margin: 32px; }
                            h1, h2 { margin-bottom: 8px; }
                            .meta { margin-bottom: 24px; }
                            .meta div { margin: 4px 0; }
                            table { width: 100%; border-collapse: collapse; margin-top: 16px; }
                            th, td { border: 1px solid #cbd5e1; padding: 8px 10px; text-align: left; }
                            th { background: #eff6ff; }
                            .note { margin-top: 20px; color: #475569; }
                        </style>
                    </head>
                    <body>
                        <h1>BulSU School Management System</h1>
                        <h2>Certificate of Registration / Advising Slip</h2>
                        <div class="meta">
                    """);

            for (Object[] row : profile.rows()) {
                builder.append("<div><strong>")
                        .append(escapeHtml(String.valueOf(row[0])))
                        .append(":</strong> ")
                        .append(escapeHtml(String.valueOf(row[1])))
                        .append("</div>");
            }

            builder.append("</div><table><thead><tr>");
            for (String columnName : documentTable.columns()) {
                builder.append("<th>").append(escapeHtml(columnName)).append("</th>");
            }
            builder.append("</tr></thead><tbody>");

            for (Object[] row : documentTable.rows()) {
                builder.append("<tr>");
                for (Object value : row) {
                    builder.append("<td>").append(escapeHtml(String.valueOf(value))).append("</td>");
                }
                builder.append("</tr>");
            }

            builder.append("""
                    </tbody></table>
                    <p class="note">Generated from the active student account. Staff or registrar may validate the enrollment status from the registrations panel.</p>
                    </body>
                    </html>
                    """);

            return builder.toString();
        }
    }

    public DashboardPageData loadPage(Role role, User user, String section) {
        try (Connection connection = DBConnection.getConnection()) {
            return switch (role) {
                case STUDENT -> loadStudentPage(connection, user, section);
                case PROFESSOR -> loadProfessorPage(connection, user, section);
                case STAFF -> loadStaffPage(connection, user, section);
                case ADMIN -> loadAdminPage(connection, user, section);
            };
        } catch (SQLException exception) {
            return buildErrorPage(role, section, exception);
        }
    }

    private DashboardPageData loadStudentPage(Connection connection, User user, String section) throws SQLException {
        TableData subjectTable = loadStudentSubjectRows(connection, user);
        List<MetricCardData> metrics = studentMetrics(subjectTable);

        return switch (section) {
            case "My Profile" -> {
                TableData profile = profileTable(connection, Role.STUDENT, user);
                yield new DashboardPageData(
                        "My Profile",
                        "View the account and student information connected to your login.",
                        metrics,
                        "Profile Details",
                        profile.columns(),
                        profile.rows(),
                        "Student Notes",
                        buildStudentNotes(connection, user)
                );
            }
            case "My Subjects" -> new DashboardPageData(
                    "My Subjects",
                    "Subjects enrolled under the current student account.",
                    metrics,
                    "Enrolled Subjects",
                    new String[]{"Subject", "Professor", "Status"},
                    toSubjectStatusRows(subjectTable),
                    "Subject Tips",
                    List.of(
                            "Use the search box to filter subjects, professors, or status.",
                            "Double-click any row to see the full record details.",
                            "Refresh after grade encoding or enrollment updates."
                    )
            );
            case "My Grades" -> new DashboardPageData(
                    "My Grades",
                    "Published grades sourced from the MySQL database.",
                    metrics,
                    "Grade Book",
                    new String[]{"Subject", "Grade", "Remarks"},
                    toGradeRows(subjectTable),
                    "Grade Status",
                    List.of(
                            "Grades marked Pending have not been encoded yet.",
                            "Posted grades are pulled directly from the grades table.",
                            "Contact your professor if a subject record looks incomplete."
                    )
            );
            case "Schedule" -> schedulePage(connection, roleSectionTitle(section), metrics, user, Role.STUDENT);
            case "COR & Advising Slip" -> {
                TableData documentTable = loadStudentDocumentRows(connection, user);
                yield new DashboardPageData(
                        "COR & Advising Slip",
                        "Registration details that can be downloaded for submission or advising.",
                        metrics,
                        "Registration Document",
                        documentTable.columns(),
                        documentTable.rows(),
                        "Document Notes",
                        List.of(
                                "Use the download button to export the current registration view.",
                                "Enrollment status is pulled from the enrollments table.",
                                "Schedule details are summarized from class_schedules when available."
                        )
                );
            }
            default -> new DashboardPageData(
                    "Student Overview",
                    "Track subjects, grades, and day-to-day schedule information.",
                    metrics,
                    "Enrolled Subjects",
                    subjectTable.columns(),
                    subjectTable.rows(),
                    "Today's Schedule",
                    buildStudentNotes(connection, user)
            );
        };
    }

    private DashboardPageData loadProfessorPage(Connection connection, User user, String section) throws SQLException {
        TableData classTable = loadProfessorClasses(connection, user);
        TableData gradebookTable = loadProfessorGradebook(connection, user);
        List<MetricCardData> metrics = professorMetrics(classTable, gradebookTable);

        return switch (section) {
            case "My Profile" -> {
                TableData profile = profileTable(connection, Role.PROFESSOR, user);
                yield new DashboardPageData(
                        "Professor Profile",
                        "Reference information for the logged-in faculty account.",
                        metrics,
                        "Profile Details",
                        profile.columns(),
                        profile.rows(),
                        "Teaching Notes",
                        List.of(
                                "Use Ctrl+R to refresh classes and gradebook data.",
                                "If a professor record is missing, the app falls back to the users table.",
                                "Search can filter by subject or student."
                        )
                );
            }
            case "My Classes" -> new DashboardPageData(
                    "My Classes",
                    "Assigned classes and current enrollment counts.",
                    metrics,
                    "Assigned Subjects",
                    classTable.columns(),
                    classTable.rows(),
                    "Classroom Summary",
                    List.of(
                            "Enrollment counts come from the enrollments table.",
                            "Class rows are filtered by professor_id.",
                            "Double-click a class row to inspect its values."
                    )
            );
            case "Gradebook" -> new DashboardPageData(
                    "Gradebook",
                    "Student grade entries under the current professor account.",
                    metrics,
                    "Encoded Grades",
                    gradebookTable.columns(),
                    gradebookTable.rows(),
                    "Gradebook Notes",
                    List.of(
                            "Pending entries mean a grade row is still null or missing.",
                            "Search supports student names, subjects, and grade values.",
                            "Refresh after saving database changes in XAMPP."
                    )
            );
            case "Schedule" -> schedulePage(connection, roleSectionTitle(section), metrics, user, Role.PROFESSOR);
            default -> new DashboardPageData(
                    "Professor Overview",
                    "Monitor assigned classes, learners, and gradebook activity.",
                    metrics,
                    "Assigned Subjects",
                    classTable.columns(),
                    classTable.rows(),
                    "Faculty Activity",
                    List.of(
                            "Assigned subject rows are filtered by professor_id.",
                            "Total learners is derived from enrollments connected to your subjects.",
                            "Use the search field for fast filtering across visible rows."
                    )
            );
        };
    }
    private DashboardPageData loadStaffPage(Connection connection, User user, String section) throws SQLException {
        List<MetricCardData> metrics = staffMetrics(connection);
        TableData usersPreview = previewUsers(connection, null, 25);
        TableData registrationPreview = loadRegistrationRecords(connection);

        return switch (section) {
            case "My Profile" -> {
                TableData profile = profileTable(connection, Role.STAFF, user);
                yield new DashboardPageData(
                        "Staff Profile",
                        "Account information and linked staff record details.",
                        metrics,
                        "Profile Details",
                        profile.columns(),
                        profile.rows(),
                        "Operations Notes",
                        List.of(
                                "Staff pages are designed for records and registration monitoring.",
                                "Missing linked staff data falls back to the users table.",
                                "Use search to narrow down operational records quickly."
                        )
                );
            }
            case "Registrations" -> new DashboardPageData(
                    "Registrations",
                    "Enrollment or student registration records detected from the database.",
                    metrics,
                    "Registration Queue",
                    registrationPreview.columns(),
                    registrationPreview.rows(),
                    "Registration Notes",
                    List.of(
                            "When the enrollments table exists, it is shown here first.",
                            "Use this page to approve or update student enrollment status.",
                            "Refresh after adding or editing records in XAMPP."
                    )
            );
            case "Schedules" -> {
                TableData schedules = loadScheduleManagementRows(connection);
                yield new DashboardPageData(
                        "Schedules",
                        "Create and maintain class schedules that reflect to students and professors.",
                        metrics,
                        "Class Schedules",
                        schedules.columns(),
                        schedules.rows(),
                        "Schedule Notes",
                        List.of(
                                "Each schedule record is linked to a class offering.",
                                "Changes here will reflect in the student and professor schedule pages.",
                                "Use Add, Edit, or Delete to maintain the timetable."
                        )
                );
            }
            case "Subjects" -> {
                TableData subjects = loadSubjectCatalogRows(connection);
                yield new DashboardPageData(
                        "Subjects",
                        "Maintain the subject catalog and academic offerings.",
                        metrics,
                        "Subject Catalog",
                        subjects.columns(),
                        subjects.rows(),
                        "Subject Notes",
                        List.of(
                                "Subject records feed classes, schedules, and registration workflows.",
                                "Use this page for subject code, title, unit, and status updates.",
                                "Refresh after creating or editing subject records."
                        )
                );
            }
            case "Records" -> new DashboardPageData(
                    "Records",
                    "A searchable view of user and system records.",
                    metrics,
                    "User Records",
                    usersPreview.columns(),
                    usersPreview.rows(),
                    "Records Notes",
                    List.of(
                            "Passwords are intentionally hidden from preview tables.",
                            "This page is useful for validating account entries after CRUD updates.",
                            "Double-click any row to view the full data snapshot."
                    )
            );
            default -> new DashboardPageData(
                    "Staff Overview",
                    "Track records, registrations, and account activity from one place.",
                    metrics,
                    "Recent Records",
                    usersPreview.columns(),
                    usersPreview.rows(),
                    "Operations Snapshot",
                    List.of(
                            "Student accounts, staff accounts, and subject counts are summarized above.",
                            "The main table previews live data from your MySQL schema.",
                            "Use Refresh or Ctrl+R to pull new values."
                    )
            );
        };
    }

    private DashboardPageData loadAdminPage(Connection connection, User user, String section) throws SQLException {
        List<MetricCardData> metrics = adminMetrics(connection);

        return switch (section) {
            case "User Accounts" -> {
                TableData accounts = previewUsers(connection, null, 25);
                yield new DashboardPageData(
                        "User Accounts",
                        "All account records stored in the users table.",
                        metrics,
                        "Accounts",
                        accounts.columns(),
                        accounts.rows(),
                        "Admin Notes",
                        List.of(
                                "Account previews exclude password fields for safety.",
                                "Use this page to validate role, email, and linked IDs.",
                                "Search filters the current table in real time."
                        )
                );
            }
            case "Students" -> rolePreviewPage(connection, metrics, "Students", Role.STUDENT);
            case "Professors" -> rolePreviewPage(connection, metrics, "Professors", Role.PROFESSOR);
            case "Staff" -> rolePreviewPage(connection, metrics, "Staff", Role.STAFF);
            case "Reports" -> {
                TableData reportTable = buildAdminReportTable(connection);
                yield new DashboardPageData(
                        "Reports",
                        "Quick count-based reporting from the available school tables.",
                        metrics,
                        "System Report",
                        reportTable.columns(),
                        reportTable.rows(),
                        "Report Notes",
                        List.of(
                                "Counts update directly from the connected MySQL schema.",
                                "If a table does not exist yet, its count falls back to 0.",
                                "This is useful while building the full database module in XAMPP."
                        )
                );
            }
            default -> {
                TableData accountTable = previewUsers(connection, null, 25);
                yield new DashboardPageData(
                        "Admin Overview",
                        "System-wide status across users, academic records, and subject offerings.",
                        metrics,
                        "Recent Accounts",
                        accountTable.columns(),
                        accountTable.rows(),
                        "System Health",
                        List.of(
                                "Admin pages are designed for fast access to records and reporting.",
                                "The dashboard stays usable even when some optional tables are missing.",
                                "Use Ctrl+R to reload the latest database state."
                        )
                );
            }
        };
    }

    private DashboardPageData rolePreviewPage(Connection connection, List<MetricCardData> metrics, String title, Role role)
            throws SQLException {
        TableData tableData = previewLinkedTableOrRoleUsers(connection, role, 25);

        return new DashboardPageData(
                title,
                "Preview data detected for " + title.toLowerCase(Locale.ROOT) + ".",
                metrics,
                title + " Records",
                tableData.columns(),
                tableData.rows(),
                "Role Notes",
                List.of(
                        "The app prefers the dedicated " + title.toLowerCase(Locale.ROOT) + " table when it exists.",
                        "If the dedicated table is missing, it falls back to filtered users rows.",
                        "Search and row inspection work on both preview sources."
                )
        );
    }

    private DashboardPageData schedulePage(Connection connection, String title, List<MetricCardData> metrics, User user, Role role)
            throws SQLException {
        TableData scheduleTable = previewSchedule(connection, role, user);

        return new DashboardPageData(
                title,
                "Schedule data preview based on the available schema.",
                metrics,
                "Schedule",
                scheduleTable.columns(),
                scheduleTable.rows(),
                "Schedule Notes",
                List.of(
                        "The app checks for schedule tables such as class_schedules, schedules, or schedule.",
                        "If no dedicated schedule table exists yet, a placeholder row is shown instead.",
                        "Refresh after creating schedule records in the database."
                )
        );
    }

    private TableData loadStudentSubjectRows(Connection connection, User user) throws SQLException {
        if (tableExists(connection, "classes") && tableExists(connection, "enrollments")) {
            Integer studentId = user.getStudentId();
            if (studentId != null) {
                StringBuilder sql = new StringBuilder("""
                        SELECT e.enrollment_id, e.class_id, e.status, c.subject_id, c.professor_id
                        FROM enrollments e
                        JOIN classes c ON e.class_id = c.class_id
                        WHERE e.student_id = ?
                        ORDER BY c.class_id DESC
                        """);

                try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                    statement.setInt(1, studentId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<Object[]> rows = new ArrayList<>();
                        while (resultSet.next()) {
                            Integer subjectId = integerValue(resultSet.getObject("subject_id"));
                            Integer professorId = integerValue(resultSet.getObject("professor_id"));
                            Integer enrollmentId = integerValue(resultSet.getObject("enrollment_id"));

                            rows.add(new Object[]{
                                resolveSubjectDisplay(connection, subjectId),
                                resolveEntityName(connection, professorId, "professors", "professor_id", "TBA"),
                                resolveEnrollmentGrade(connection, enrollmentId)
                            });
                        }

                        if (!rows.isEmpty()) {
                            return new TableData(new String[]{"Subject", "Professor", "Grade"}, rows);
                        }
                    }
                } catch (SQLException ignored) {
                    // Fall back to the legacy schema below.
                }
            }
        }

        return legacyLoadStudentSubjectRows(connection, user);
    }

    private TableData loadProfessorClasses(Connection connection, User user) throws SQLException {
        if (tableExists(connection, "classes")) {
            Integer professorId = user.getProfessorId();
            if (professorId != null) {
                String sql = """
                        SELECT c.class_id, c.subject_id, c.section_id, c.status, COUNT(e.enrollment_id) AS student_count
                        FROM classes c
                        LEFT JOIN enrollments e ON c.class_id = e.class_id
                        WHERE c.professor_id = ?
                        GROUP BY c.class_id, c.subject_id, c.section_id, c.status
                        ORDER BY c.class_id DESC
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, professorId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<Object[]> rows = new ArrayList<>();
                        while (resultSet.next()) {
                            rows.add(new Object[]{
                                resultSet.getInt("class_id"),
                                resolveSubjectDisplay(connection, integerValue(resultSet.getObject("subject_id"))),
                                resolveSectionName(connection, integerValue(resultSet.getObject("section_id"))),
                                resultSet.getInt("student_count"),
                                safeString(resultSet.getString("status"), "open")
                            });
                        }

                        if (!rows.isEmpty()) {
                            return new TableData(new String[]{"Class Id", "Subject", "Section", "Students", "Status"}, rows);
                        }
                    }
                } catch (SQLException ignored) {
                    // Fall back to the legacy schema below.
                }
            }
        }

        return legacyLoadProfessorClasses(connection, user);
    }

    private TableData loadProfessorGradebook(Connection connection, User user) throws SQLException {
        if (tableExists(connection, "classes") && tableExists(connection, "enrollments")) {
            Integer professorId = user.getProfessorId();
            if (professorId != null) {
                String sql = """
                        SELECT e.enrollment_id, e.student_id, e.status, c.subject_id
                        FROM classes c
                        JOIN enrollments e ON c.class_id = e.class_id
                        WHERE c.professor_id = ?
                        ORDER BY c.class_id DESC, e.enrollment_id DESC
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, professorId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<Object[]> rows = new ArrayList<>();
                        while (resultSet.next()) {
                            Integer enrollmentId = integerValue(resultSet.getObject("enrollment_id"));
                            Integer studentId = integerValue(resultSet.getObject("student_id"));
                            GradeInput gradeInput = loadGradeInput(connection, enrollmentId);

                            rows.add(new Object[]{
                                enrollmentId,
                                resolveSubjectDisplay(connection, integerValue(resultSet.getObject("subject_id"))),
                                resolveEntityName(connection, studentId, "students", "student_id",
                                        safeString(resolveUsernameByStudentId(connection, studentId), "Unknown Student")),
                                decimalOrDash(gradeInput.prelim()),
                                decimalOrDash(gradeInput.midterm()),
                                decimalOrDash(gradeInput.finals()),
                                decimalOrDash(gradeInput.resolvedFinalGrade()),
                                safeString(resultSet.getString("status"), "enrolled")
                            });
                        }

                        if (!rows.isEmpty()) {
                            return new TableData(
                                    new String[]{"Enrollment Id", "Subject", "Student", "Prelim", "Midterm", "Finals", "Final Grade", "Status"},
                                    rows
                            );
                        }
                    }
                } catch (SQLException ignored) {
                    // Fall back to the legacy schema below.
                }
            }
        }

        return legacyLoadProfessorGradebook(connection, user);
    }

    private List<MetricCardData> studentMetrics(TableData subjectTable) {
        int subjectCount = countMeaningfulRows(subjectTable);
        int postedGrades = 0;
        List<BigDecimal> numericGrades = new ArrayList<>();

        for (Object[] row : subjectTable.rows()) {
            if (row.length < 3) {
                continue;
            }

            String gradeValue = String.valueOf(row[2]);
            if (!"Pending".equalsIgnoreCase(gradeValue) && !"-".equals(gradeValue)) {
                postedGrades++;
                BigDecimal parsed = tryParseDecimal(gradeValue);
                if (parsed != null) {
                    numericGrades.add(parsed);
                }
            }
        }

        String averageGrade = numericGrades.isEmpty()
                ? "N/A"
                : numericGrades.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(numericGrades.size()), 2, RoundingMode.HALF_UP)
                        .toPlainString();

        return List.of(
                new MetricCardData("Enrolled Subjects", String.valueOf(subjectCount), "Subjects linked to the logged-in account"),
                new MetricCardData("Posted Grades", String.valueOf(postedGrades), "Grades already available in the database"),
                new MetricCardData("Average Grade", averageGrade, "Calculated from numeric grade entries only")
        );
    }

    private List<MetricCardData> professorMetrics(TableData classes, TableData gradebook) {
        int classCount = countMeaningfulRows(classes);
        int learnerCount = 0;
        for (Object[] row : classes.rows()) {
            for (Object value : row) {
                if (value instanceof Number number) {
                    learnerCount += number.intValue();
                    break;
                }
            }
        }

        return List.of(
                new MetricCardData("Assigned Classes", String.valueOf(classCount), "Subjects currently assigned to you"),
                new MetricCardData("Tracked Learners", String.valueOf(learnerCount), "Enrollment totals across your classes"),
                new MetricCardData("Grade Rows", String.valueOf(countMeaningfulRows(gradebook)), "Gradebook entries pulled from MySQL")
        );
    }

    private List<MetricCardData> staffMetrics(Connection connection) throws SQLException {
        return List.of(
                new MetricCardData("Student Accounts", String.valueOf(countUsersByRole(connection, "student")), "Detected in the users table"),
                new MetricCardData("Staff Accounts", String.valueOf(countUsersByRole(connection, "staff")), "Staff role accounts stored in MySQL"),
                new MetricCardData("Subject Records", String.valueOf(countTableRows(connection, "subjects")), "Available academic offerings")
        );
    }

    private List<MetricCardData> adminMetrics(Connection connection) throws SQLException {
        return List.of(
                new MetricCardData("Total Users", String.valueOf(countTableRows(connection, "users")), "All accounts registered in the system"),
                new MetricCardData("Student Users", String.valueOf(countUsersByRole(connection, "student")), "Accounts tagged as student"),
                new MetricCardData("Active Subjects", String.valueOf(countTableRows(connection, "subjects")), "Subjects currently stored in the database")
        );
    }

    private TableData profileTable(Connection connection, Role role, User user) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{"Full Name", safeString(user.getDisplayName(), "-")});
        rows.add(new Object[]{"Username", safeString(user.getUsername(), "-")});
        rows.add(new Object[]{"Email", safeString(user.getEmail(), "-")});
        rows.add(new Object[]{"Role", role.getDisplayName()});
        rows.add(new Object[]{"User ID", user.getUserId() > 0 ? user.getUserId() : "-"});

        Integer referenceId = referenceId(role, user);
        if (referenceId != null) {
            rows.add(new Object[]{role.getDisplayName() + " ID", referenceId});
        }

        String linkedTable = linkedTable(role, connection);
        String linkedIdColumn = linkedIdColumn(role);

        if (linkedTable != null && referenceId != null) {
            String sql = "SELECT * FROM " + linkedTable + " WHERE " + linkedIdColumn + " = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, referenceId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        Set<String> seen = new HashSet<>();
                        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
                            String column = metaData.getColumnLabel(columnIndex);
                            if (shouldSkipColumn(column) || seen.contains(column.toLowerCase(Locale.ROOT))) {
                                continue;
                            }
                            Object value = resultSet.getObject(columnIndex);
                            if (value == null || value.toString().isBlank()) {
                                continue;
                            }
                            seen.add(column.toLowerCase(Locale.ROOT));
                            rows.add(new Object[]{formatColumn(column), value});
                        }
                    }
                }
            } catch (SQLException ignored) {
                // The profile card still works with the base account fields above.
            }
        }

        return new TableData(new String[]{"Field", "Value"}, rows);
    }

    private TableData previewUsers(Connection connection, String roleFilter, int limit) throws SQLException {
        if (!tableExists(connection, "users")) {
            return placeholder("Field", "Value", "Status");
        }

        String sql = roleFilter == null
                ? "SELECT * FROM users ORDER BY user_id DESC LIMIT ?"
                : "SELECT * FROM users WHERE LOWER(role) = ? ORDER BY user_id DESC LIMIT ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            if (roleFilter != null) {
                statement.setString(parameterIndex++, roleFilter);
            }
            statement.setInt(parameterIndex, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSetTable(resultSet, Set.of("password"));
            }
        } catch (SQLException exception) {
            return placeholder("Field", "Value", "Status");
        }
    }

    private TableData previewLinkedTableOrRoleUsers(Connection connection, Role role, int limit) throws SQLException {
        String linkedTable = linkedTable(role, connection);
        if (linkedTable != null) {
            return previewTable(connection, linkedTable, limit);
        }
        return previewUsers(connection, role.getDatabaseValue(), limit);
    }

    private TableData previewPreferredTable(Connection connection, int limit, String... candidates) throws SQLException {
        for (String candidate : candidates) {
            String existingTable = firstExistingTable(connection, candidate);
            if (existingTable != null) {
                return previewTable(connection, existingTable, limit);
            }
        }
        return placeholder("Record", "Value", "Status");
    }

    private TableData previewTable(Connection connection, String tableName, int limit) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " LIMIT " + limit;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSetTable(resultSet, Set.of("password"));
        } catch (SQLException exception) {
            return placeholder("Record", "Value", "Status");
        }
    }

    private TableData loadStudentDocumentRows(Connection connection, User user) throws SQLException {
        if (!tableExists(connection, "classes") || !tableExists(connection, "enrollments")) {
            return placeholder("Subject Code", "Subject", "Units", "Section", "Professor", "Schedule", "Enrollment Status");
        }

        Integer studentId = user.getStudentId();
        if (studentId == null) {
            return placeholder("Subject Code", "Subject", "Units", "Section", "Professor", "Schedule", "Enrollment Status");
        }

        String sql = """
                SELECT e.enrollment_id, e.class_id, e.status, c.subject_id, c.professor_id, c.section_id, c.semester_id, c.school_year_id
                FROM enrollments e
                JOIN classes c ON e.class_id = c.class_id
                WHERE e.student_id = ?
                ORDER BY c.class_id DESC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (resultSet.next()) {
                    Integer classId = integerValue(resultSet.getObject("class_id"));
                    Integer subjectId = integerValue(resultSet.getObject("subject_id"));
                    Integer professorId = integerValue(resultSet.getObject("professor_id"));
                    Integer sectionId = integerValue(resultSet.getObject("section_id"));
                    Integer semesterId = integerValue(resultSet.getObject("semester_id"));
                    Integer schoolYearId = integerValue(resultSet.getObject("school_year_id"));

                    rows.add(new Object[]{
                        safeString(resolveSubjectCode(connection, subjectId), "-"),
                        resolveSubjectName(connection, subjectId),
                        resolveSubjectUnits(connection, subjectId),
                        resolveSectionName(connection, sectionId),
                        resolveEntityName(connection, professorId, "professors", "professor_id", "TBA"),
                        buildClassScheduleSummary(connection, classId),
                        safeString(resultSet.getString("status"), "enrolled"),
                        resolveSemesterName(connection, semesterId),
                        resolveSchoolYearName(connection, schoolYearId)
                    });
                }

                if (!rows.isEmpty()) {
                    return new TableData(
                            new String[]{"Subject Code", "Subject", "Units", "Section", "Professor", "Schedule", "Enrollment Status", "Semester", "School Year"},
                            rows
                    );
                }
            }
        } catch (SQLException ignored) {
            // Fall back to an empty placeholder below.
        }

        return placeholder("Subject Code", "Subject", "Units", "Section", "Professor", "Schedule", "Enrollment Status");
    }

    private TableData loadRegistrationRecords(Connection connection) throws SQLException {
        if (!tableExists(connection, "enrollments")) {
            return previewPreferredTable(connection, 20, "students");
        }

        String sql = "SELECT enrollment_id, student_id, class_id, status FROM enrollments ORDER BY enrollment_id DESC LIMIT 50";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                Integer studentId = integerValue(resultSet.getObject("student_id"));
                Integer classId = integerValue(resultSet.getObject("class_id"));
                Integer subjectId = integerValue(lookupColumnValue(connection, "classes", "class_id", classId, "subject_id"));
                Integer sectionId = integerValue(lookupColumnValue(connection, "classes", "class_id", classId, "section_id"));
                Integer semesterId = integerValue(lookupColumnValue(connection, "classes", "class_id", classId, "semester_id"));
                Integer schoolYearId = integerValue(lookupColumnValue(connection, "classes", "class_id", classId, "school_year_id"));

                rows.add(new Object[]{
                    resultSet.getInt("enrollment_id"),
                    resolveEntityName(connection, studentId, "students", "student_id",
                            safeString(resolveUsernameByStudentId(connection, studentId), "Unknown Student")),
                    resolveStudentCourseName(connection, studentId),
                    resolveSectionName(connection, sectionId),
                    resolveSubjectDisplay(connection, subjectId),
                    resolveSemesterName(connection, semesterId),
                    resolveSchoolYearName(connection, schoolYearId),
                    safeString(resultSet.getString("status"), "enrolled")
                });
            }

            return rows.isEmpty()
                    ? placeholder("Enrollment Id", "Student", "Course", "Section", "Subject", "Semester", "School Year", "Status")
                    : new TableData(
                            new String[]{"Enrollment Id", "Student", "Course", "Section", "Subject", "Semester", "School Year", "Status"},
                            rows
                    );
        } catch (SQLException exception) {
            return placeholder("Enrollment Id", "Student", "Course", "Section", "Subject", "Semester", "School Year", "Status");
        }
    }

    private TableData loadScheduleManagementRows(Connection connection) throws SQLException {
        return loadScheduleRows(connection, Role.STAFF, null);
    }

    private TableData loadSubjectCatalogRows(Connection connection) throws SQLException {
        if (!tableExists(connection, "subjects")) {
            return placeholder("Subject Id", "Code", "Subject", "Units", "Department Id", "Department", "Status");
        }

        String sql = "SELECT subject_id FROM subjects ORDER BY subject_id DESC LIMIT 50";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                Integer subjectId = integerValue(resultSet.getObject("subject_id"));
                Integer departmentId = integerValue(lookupColumnValue(connection, "subjects", "subject_id", subjectId, "department_id"));
                Object statusValue = lookupColumnValue(connection, "subjects", "subject_id", subjectId, "status");
                rows.add(new Object[]{
                    subjectId,
                    safeString(resolveSubjectCode(connection, subjectId), "-"),
                    resolveSubjectName(connection, subjectId),
                    resolveSubjectUnits(connection, subjectId),
                    departmentId == null ? "-" : departmentId,
                    resolveDepartmentName(connection, departmentId),
                    safeString(statusValue == null ? null : statusValue.toString(), "active")
                });
            }

            return rows.isEmpty()
                    ? placeholder("Subject Id", "Code", "Subject", "Units", "Department Id", "Department", "Status")
                    : new TableData(new String[]{"Subject Id", "Code", "Subject", "Units", "Department Id", "Department", "Status"}, rows);
        } catch (SQLException exception) {
            return placeholder("Subject Id", "Code", "Subject", "Units", "Department Id", "Department", "Status");
        }
    }

    private TableData previewSchedule(Connection connection, Role role, User user) throws SQLException {
        return loadScheduleRows(connection, role, user);
    }

    private TableData loadScheduleRows(Connection connection, Role role, User user) throws SQLException {
        if (tableExists(connection, "class_schedules") && tableExists(connection, "classes")) {
            StringBuilder sql = new StringBuilder("""
                    SELECT cs.class_schedule_id, cs.class_id, cs.day_of_week, cs.start_time, cs.end_time, cs.room,
                           c.subject_id, c.section_id, c.professor_id, c.status AS class_status
                    FROM class_schedules cs
                    JOIN classes c ON cs.class_id = c.class_id
                    """);
            List<Object> parameters = new ArrayList<>();

            if (role == Role.STUDENT && user != null && user.getStudentId() != null && tableExists(connection, "enrollments")) {
                sql.append(" JOIN enrollments e ON e.class_id = c.class_id WHERE e.student_id = ?");
                parameters.add(user.getStudentId());
            } else if (role == Role.PROFESSOR && user != null && user.getProfessorId() != null) {
                sql.append(" WHERE c.professor_id = ?");
                parameters.add(user.getProfessorId());
            }

            sql.append(" ORDER BY cs.day_of_week, cs.start_time LIMIT 50");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                for (int index = 0; index < parameters.size(); index++) {
                    statement.setObject(index + 1, parameters.get(index));
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Object[]> rows = new ArrayList<>();
                    while (resultSet.next()) {
                        rows.add(new Object[]{
                            resultSet.getInt("class_schedule_id"),
                            resultSet.getInt("class_id"),
                            resolveSubjectDisplay(connection, integerValue(resultSet.getObject("subject_id"))),
                            resolveSectionName(connection, integerValue(resultSet.getObject("section_id"))),
                            resolveEntityName(connection, resultSet.getObject("professor_id"), "professors", "professor_id", "TBA"),
                            safeString(resultSet.getString("day_of_week"), "-"),
                            safeString(resultSet.getString("start_time"), "-") + " - " + safeString(resultSet.getString("end_time"), "-"),
                            safeString(resultSet.getString("room"), "-"),
                            safeString(resultSet.getString("class_status"), "open")
                        });
                    }

                    if (!rows.isEmpty()) {
                        return new TableData(
                                new String[]{"Schedule Id", "Class Id", "Subject", "Section", "Professor", "Day", "Time", "Room", "Status"},
                                rows
                        );
                    }
                }
            } catch (SQLException ignored) {
                // Fall back to the legacy schema below.
            }
        }

        return legacyPreviewSchedule(connection, role, user);
    }

    private TableData legacyLoadStudentSubjectRows(Connection connection, User user) throws SQLException {
        if (!tableExists(connection, "users") || !tableExists(connection, "enrollments") || !tableExists(connection, "subjects")) {
            return placeholder("Subject", "Professor", "Grade");
        }

        String baseSql = """
                SELECT sub.subject_name, sub.professor_id, g.grade_value
                FROM users u
                JOIN enrollments e ON u.student_id = e.student_id
                JOIN subjects sub ON e.subject_id = sub.subject_id
                LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id
                WHERE %s
                ORDER BY sub.subject_name
                """;

        boolean useUserId = user.getUserId() > 0;
        String sql = baseSql.formatted(useUserId ? "u.user_id = ?" : "u.username = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (useUserId) {
                statement.setInt(1, user.getUserId());
            } else {
                statement.setString(1, user.getUsername());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (resultSet.next()) {
                    String subjectName = safeString(resultSet.getString("subject_name"), "Unknown Subject");
                    Object professorId = resultSet.getObject("professor_id");
                    String professorName = resolveEntityName(connection, professorId, "professors", "professor_id", "TBA");
                    String grade = safeString(resultSet.getString("grade_value"), "Pending");
                    rows.add(new Object[]{subjectName, professorName, grade});
                }

                if (rows.isEmpty()) {
                    return placeholder("Subject", "Professor", "Grade");
                }

                return new TableData(new String[]{"Subject", "Professor", "Grade"}, rows);
            }
        } catch (SQLException exception) {
            return placeholder("Subject", "Professor", "Grade");
        }
    }

    private TableData legacyLoadProfessorClasses(Connection connection, User user) throws SQLException {
        if (!tableExists(connection, "subjects")) {
            return placeholder("Subject", "Students", "Status");
        }

        Integer professorId = user.getProfessorId();
        if (professorId == null) {
            return placeholder("Subject", "Students", "Status");
        }

        String sql = """
                SELECT s.subject_name, COUNT(e.enrollment_id) AS student_count
                FROM subjects s
                LEFT JOIN enrollments e ON s.subject_id = e.subject_id
                WHERE s.professor_id = ?
                GROUP BY s.subject_id, s.subject_name
                ORDER BY s.subject_name
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, professorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (resultSet.next()) {
                    int studentCount = resultSet.getInt("student_count");
                    rows.add(new Object[]{
                        safeString(resultSet.getString("subject_name"), "Unknown Subject"),
                        studentCount,
                        studentCount > 0 ? "Active" : "No Enrollees"
                    });
                }

                if (rows.isEmpty()) {
                    return placeholder("Subject", "Students", "Status");
                }

                return new TableData(new String[]{"Subject", "Students", "Status"}, rows);
            }
        } catch (SQLException exception) {
            return placeholder("Subject", "Students", "Status");
        }
    }

    private TableData legacyLoadProfessorGradebook(Connection connection, User user) throws SQLException {
        if (!tableExists(connection, "subjects") || !tableExists(connection, "enrollments")) {
            return placeholder("Subject", "Student", "Grade");
        }

        Integer professorId = user.getProfessorId();
        if (professorId == null) {
            return placeholder("Subject", "Student", "Grade");
        }

        String sql = """
                SELECT s.subject_name, e.student_id, g.grade_value
                FROM subjects s
                JOIN enrollments e ON s.subject_id = e.subject_id
                LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id
                WHERE s.professor_id = ?
                ORDER BY s.subject_name
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, professorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Object[]> rows = new ArrayList<>();
                while (resultSet.next()) {
                    Integer studentId = integerValue(resultSet.getObject("student_id"));
                    String studentName = resolveEntityName(connection, studentId, "students", "student_id",
                            safeString(resolveUsernameByStudentId(connection, studentId), "Unknown Student"));

                    rows.add(new Object[]{
                        safeString(resultSet.getString("subject_name"), "Unknown Subject"),
                        studentName,
                        safeString(resultSet.getString("grade_value"), "Pending")
                    });
                }

                if (rows.isEmpty()) {
                    return placeholder("Subject", "Student", "Grade");
                }

                return new TableData(new String[]{"Subject", "Student", "Grade"}, rows);
            }
        } catch (SQLException exception) {
            return placeholder("Subject", "Student", "Grade");
        }
    }

    private TableData legacyPreviewSchedule(Connection connection, Role role, User user) throws SQLException {
        String scheduleTable = firstExistingTable(connection, "class_schedules", "schedules", "schedule");
        if (scheduleTable == null) {
            return placeholder("Day", "Time", "Notes");
        }

        Set<String> columns = tableColumns(connection, scheduleTable);
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(scheduleTable);
        List<Object> parameters = new ArrayList<>();

        String filterColumn = switch (role) {
            case STUDENT -> columns.contains("student_id") ? "student_id" : null;
            case PROFESSOR -> columns.contains("professor_id") ? "professor_id" : null;
            case STAFF -> columns.contains("staff_id") ? "staff_id" : null;
            case ADMIN -> null;
        };

        Integer referenceId = user == null ? null : referenceId(role, user);
        if (filterColumn != null && referenceId != null) {
            sql.append(" WHERE ").append(filterColumn).append(" = ?");
            parameters.add(referenceId);
        }

        sql.append(" LIMIT 20");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSetTable(resultSet, Set.of("password"));
            }
        } catch (SQLException exception) {
            return placeholder("Day", "Time", "Notes");
        }
    }

    private GradeInput loadGradeInput(Connection connection, Integer enrollmentId) throws SQLException {
        if (enrollmentId == null || !tableExists(connection, "grades")) {
            return new GradeInput(null, null, null, null);
        }

        String sql = "SELECT * FROM grades WHERE enrollment_id = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return new GradeInput(null, null, null, null);
                }

                Set<String> columns = resultSetColumns(resultSet.getMetaData());
                BigDecimal finalGrade = null;
                if (columns.contains("final_grade")) {
                    finalGrade = resultSet.getBigDecimal("final_grade");
                } else if (columns.contains("grade_value")) {
                    finalGrade = resultSet.getBigDecimal("grade_value");
                }

                return new GradeInput(
                        columns.contains("prelim") ? resultSet.getBigDecimal("prelim") : null,
                        columns.contains("midterm") ? resultSet.getBigDecimal("midterm") : null,
                        columns.contains("finals") ? resultSet.getBigDecimal("finals") : null,
                        finalGrade
                );
            }
        }
    }

    private String resolveEnrollmentGrade(Connection connection, Integer enrollmentId) throws SQLException {
        BigDecimal value = loadGradeInput(connection, enrollmentId).resolvedFinalGrade();
        return value == null ? "Pending" : value.stripTrailingZeros().toPlainString();
    }

    private String decimalOrDash(BigDecimal value) {
        return value == null ? "-" : value.stripTrailingZeros().toPlainString();
    }

    private Object lookupColumnValue(Connection connection, String tableName, String idColumn, Object id, String... candidateColumns)
            throws SQLException {
        if (id == null) {
            return null;
        }

        String existingTable = firstExistingTable(connection, tableName);
        if (existingTable == null) {
            return null;
        }

        Set<String> columns = tableColumns(connection, existingTable);
        String targetColumn = firstAvailableColumn(columns, candidateColumns);
        if (targetColumn == null || !columns.contains(idColumn.toLowerCase(Locale.ROOT))) {
            return null;
        }

        String sql = "SELECT " + targetColumn + " FROM " + existingTable + " WHERE " + idColumn + " = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getObject(1) : null;
            }
        }
    }

    private String resolveSubjectCode(Connection connection, Integer subjectId) throws SQLException {
        Object value = lookupColumnValue(connection, "subjects", "subject_id", subjectId, "subject_code");
        return value == null ? null : value.toString();
    }

    private String resolveSubjectName(Connection connection, Integer subjectId) throws SQLException {
        Object name = lookupColumnValue(connection, "subjects", "subject_id", subjectId, "subject_name", "name");
        return safeString(name == null ? null : name.toString(), "Unknown Subject");
    }

    private String resolveSubjectDisplay(Connection connection, Integer subjectId) throws SQLException {
        String code = resolveSubjectCode(connection, subjectId);
        String name = resolveSubjectName(connection, subjectId);
        return code == null || code.isBlank() ? name : code + " - " + name;
    }

    private String resolveSubjectUnits(Connection connection, Integer subjectId) throws SQLException {
        Object value = lookupColumnValue(connection, "subjects", "subject_id", subjectId, "units");
        return value == null ? "-" : value.toString();
    }

    private String resolveSectionName(Connection connection, Integer sectionId) throws SQLException {
        Object value = lookupColumnValue(connection, "sections", "section_id", sectionId, "section_name", "name");
        return safeString(value == null ? null : value.toString(), "-");
    }

    private String resolveSemesterName(Connection connection, Integer semesterId) throws SQLException {
        Object value = lookupColumnValue(connection, "semesters", "semester_id", semesterId, "semester_name", "name");
        return safeString(value == null ? null : value.toString(), "-");
    }

    private String resolveSchoolYearName(Connection connection, Integer schoolYearId) throws SQLException {
        Object value = lookupColumnValue(connection, "school_years", "school_year_id", schoolYearId, "school_year", "name");
        return safeString(value == null ? null : value.toString(), "-");
    }

    private String resolveDepartmentName(Connection connection, Integer departmentId) throws SQLException {
        Object value = lookupColumnValue(connection, "departments", "department_id", departmentId, "department_name", "name");
        return safeString(value == null ? null : value.toString(), "-");
    }

    private String resolveStudentCourseName(Connection connection, Integer studentId) throws SQLException {
        Object directCourse = lookupColumnValue(connection, "students", "student_id", studentId,
                "course", "course_name", "program", "program_name");
        if (directCourse != null) {
            return directCourse.toString();
        }

        Integer courseId = integerValue(lookupColumnValue(connection, "students", "student_id", studentId, "course_id"));
        if (courseId != null) {
            Object courseName = lookupColumnValue(connection, "courses", "course_id", courseId, "course_name", "course_code", "name");
            if (courseName != null) {
                return courseName.toString();
            }
        }

        return "-";
    }

    private String buildClassScheduleSummary(Connection connection, Integer classId) throws SQLException {
        if (classId == null || !tableExists(connection, "class_schedules")) {
            return "-";
        }

        String sql = """
                SELECT day_of_week, start_time, end_time, room
                FROM class_schedules
                WHERE class_id = ?
                ORDER BY day_of_week, start_time
                LIMIT 3
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> slots = new ArrayList<>();
                while (resultSet.next()) {
                    slots.add(
                            safeString(resultSet.getString("day_of_week"), "Day")
                            + " "
                            + safeString(resultSet.getString("start_time"), "-")
                            + "-"
                            + safeString(resultSet.getString("end_time"), "-")
                            + " "
                            + safeString(resultSet.getString("room"), "")
                    );
                }
                return slots.isEmpty() ? "-" : String.join(" | ", slots);
            }
        }
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private TableData buildAdminReportTable(Connection connection) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{"Student Accounts", countUsersByRole(connection, "student"), "Filtered from users.role"});
        rows.add(new Object[]{"Professor Accounts", countUsersByRole(connection, "professor"), "Filtered from users.role"});
        rows.add(new Object[]{"Staff Accounts", countUsersByRole(connection, "staff"), "Filtered from users.role"});
        rows.add(new Object[]{"Admin Accounts", countUsersByRole(connection, "admin"), "Filtered from users.role"});
        rows.add(new Object[]{"Students Table", countTableRows(connection, "students"), "Dedicated student records"});
        rows.add(new Object[]{"Professors Table", countTableRows(connection, "professors"), "Dedicated professor records"});
        rows.add(new Object[]{"Staffs Table", countFirstAvailableTable(connection, "staffs", "staff"), "Dedicated staff records"});
        rows.add(new Object[]{"Subjects", countTableRows(connection, "subjects"), "Subject offerings"});
        rows.add(new Object[]{"Enrollments", countTableRows(connection, "enrollments"), "Enrollment records"});
        rows.add(new Object[]{"Grades", countTableRows(connection, "grades"), "Published grade records"});

        return new TableData(new String[]{"Category", "Count", "Notes"}, rows);
    }

    private List<String> buildStudentNotes(Connection connection, User user) throws SQLException {
        List<String> notes = new ArrayList<>();
        notes.add("Logged in as " + user.getDisplayName() + ".");
        notes.add("Use the navigation buttons on the left to switch pages.");
        notes.add("Search filters the visible table instantly while you type.");

        String scheduleTable = firstExistingTable(connection, "class_schedules", "schedules", "schedule");
        if (scheduleTable == null) {
            notes.add("No dedicated schedule table was detected yet in the database.");
        } else {
            notes.add("Schedule data source detected: " + scheduleTable + ".");
        }

        return notes;
    }

    private DashboardPageData buildErrorPage(Role role, String section, Exception exception) {
        List<MetricCardData> metrics = List.of(
                new MetricCardData("Database", "Offline", "Unable to reach MySQL/XAMPP from the application"),
                new MetricCardData("Section", section, "The requested page could not be loaded"),
                new MetricCardData("Role", role.getDisplayName(), "Authenticated UI theme still loaded successfully")
        );

        return new DashboardPageData(
                roleSectionTitle(section),
                "The GUI loaded, but database data could not be fetched.",
                metrics,
                "Error Details",
                new String[]{"Status", "Message", "Action"},
                List.<Object[]>of(new Object[]{
                    "Connection Failed",
                    exception.getMessage(),
                    "Check XAMPP MySQL, database name, and schema tables."
                }),
                "Troubleshooting",
                List.of(
                        "Verify that XAMPP MySQL is running.",
                        "Make sure the database name is school_management_system.",
                        "Confirm the users table exists and has the expected login accounts."
                )
        );
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        Set<String> columns = resultSetColumns(resultSet.getMetaData());
        User user = new User();

        if (columns.contains("user_id")) {
            user.setUserId(resultSet.getInt("user_id"));
        }
        if (columns.contains("username")) {
            user.setUsername(resultSet.getString("username"));
        }
        if (columns.contains("email")) {
            user.setEmail(resultSet.getString("email"));
        }
        if (columns.contains("password")) {
            user.setPassword(resultSet.getString("password"));
        }
        if (columns.contains("role")) {
            user.setRole(resultSet.getString("role"));
        }
        if (columns.contains("student_id")) {
            user.setStudentId(integerValue(resultSet.getObject("student_id")));
        }
        if (columns.contains("professor_id")) {
            user.setProfessorId(integerValue(resultSet.getObject("professor_id")));
        }
        if (columns.contains("admin_id")) {
            user.setAdminId(integerValue(resultSet.getObject("admin_id")));
        }
        if (columns.contains("staff_id")) {
            user.setStaffId(integerValue(resultSet.getObject("staff_id")));
        }
        user.setFullName(extractDisplayName(resultSet, columns));

        return user;
    }

    private String resolveFullName(Connection connection, Role role, User user) throws SQLException {
        Integer referenceId = referenceId(role, user);
        String linkedTable = linkedTable(role, connection);
        String linkedIdColumn = linkedIdColumn(role);

        if (linkedTable == null || referenceId == null) {
            return prettifyUsername(user.getUsername());
        }

        String sql = "SELECT * FROM " + linkedTable + " WHERE " + linkedIdColumn + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, referenceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractDisplayName(resultSet, resultSetColumns(resultSet.getMetaData()));
                }
            }
        } catch (SQLException ignored) {
            // The username fallback below is enough for incomplete schemas.
        }

        return prettifyUsername(user.getUsername());
    }

    private String resolveEntityName(Connection connection, Object entityId, String tableCandidate, String idColumn, String fallback)
            throws SQLException {
        Integer numericId = integerValue(entityId);
        if (numericId == null) {
            return fallback;
        }

        String table = firstExistingTable(connection, tableCandidate);
        if (table == null) {
            return fallback;
        }

        String sql = "SELECT * FROM " + table + " WHERE " + idColumn + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, numericId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = extractDisplayName(resultSet, resultSetColumns(resultSet.getMetaData()));
                    if (name != null && !name.isBlank()) {
                        return name;
                    }
                }
            }
        } catch (SQLException ignored) {
            // Use fallback below.
        }

        return fallback;
    }

    private String resolveUsernameByStudentId(Connection connection, Integer studentId) throws SQLException {
        if (studentId == null || !tableExists(connection, "users")) {
            return null;
        }

        String sql = "SELECT username FROM users WHERE student_id = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("username") : null;
            }
        }
    }
    private TableData resultSetTable(ResultSet resultSet, Set<String> excludedColumns) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<Integer> includedIndexes = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            String column = metaData.getColumnLabel(columnIndex);
            if (excludedColumns.contains(column.toLowerCase(Locale.ROOT))) {
                continue;
            }
            includedIndexes.add(columnIndex);
            columnNames.add(formatColumn(column));
        }

        List<Object[]> rows = new ArrayList<>();
        while (resultSet.next()) {
            Object[] row = new Object[includedIndexes.size()];
            for (int index = 0; index < includedIndexes.size(); index++) {
                Object value = resultSet.getObject(includedIndexes.get(index));
                row[index] = value == null || value.toString().isBlank() ? "-" : value;
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            return placeholder(columnNames.toArray(String[]::new));
        }

        return new TableData(columnNames.toArray(String[]::new), rows);
    }

    private TableData placeholder(String... columns) {
        String[] safeColumns = columns.length == 0 ? new String[]{"Info"} : columns;
        Object[] placeholderRow = new Object[safeColumns.length];
        placeholderRow[0] = "No data available";
        for (int index = 1; index < placeholderRow.length; index++) {
            placeholderRow[index] = "-";
        }
        return new TableData(safeColumns, List.<Object[]>of(placeholderRow));
    }

    private List<Object[]> toSubjectStatusRows(TableData subjectTable) {
        List<Object[]> rows = new ArrayList<>();
        for (Object[] row : subjectTable.rows()) {
            String grade = row.length > 2 ? String.valueOf(row[2]) : "Pending";
            String status = "Pending".equalsIgnoreCase(grade) ? "In Progress" : "Grade Posted";
            rows.add(new Object[]{row[0], row[1], status});
        }
        return rows;
    }

    private List<Object[]> toGradeRows(TableData subjectTable) {
        List<Object[]> rows = new ArrayList<>();
        for (Object[] row : subjectTable.rows()) {
            String grade = row.length > 2 ? String.valueOf(row[2]) : "Pending";
            rows.add(new Object[]{
                row[0],
                grade,
                "Pending".equalsIgnoreCase(grade) ? "Waiting for encoding" : "Posted"
            });
        }
        return rows;
    }

    private int countUsersByRole(Connection connection, String roleValue) throws SQLException {
        if (!tableExists(connection, "users")) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(role) = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    private int countTableRows(Connection connection, String tableName) throws SQLException {
        String existingTable = firstExistingTable(connection, tableName);
        if (existingTable == null) {
            return 0;
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + existingTable)) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private int countFirstAvailableTable(Connection connection, String... candidates) throws SQLException {
        for (String candidate : candidates) {
            String table = firstExistingTable(connection, candidate);
            if (table != null) {
                return countTableRows(connection, table);
            }
        }
        return 0;
    }

    private void addInsertValue(Set<String> columns, List<String> insertColumns, List<Object> values, String column, Object value) {
        if (column == null || !columns.contains(column) || value == null) {
            return;
        }
        insertColumns.add(column);
        values.add(value);
    }

    private void addUpdateValue(Set<String> columns, List<String> assignments, List<Object> values, String column, Object value) {
        if (column == null || !columns.contains(column)) {
            return;
        }
        assignments.add(column + " = ?");
        values.add(value);
    }

    private void bindParameters(PreparedStatement statement, List<Object> values) throws SQLException {
        for (int index = 0; index < values.size(); index++) {
            statement.setObject(index + 1, values.get(index));
        }
    }

    private String normalizeRoleValue(String roleValue) {
        if (roleValue == null || roleValue.isBlank()) {
            return Role.STUDENT.getDatabaseValue();
        }
        return Role.fromDatabaseValue(roleValue).getDatabaseValue();
    }

    private String firstAvailableColumn(Set<String> columns, String... candidates) {
        if (columns == null) {
            return null;
        }

        for (String candidate : candidates) {
            if (candidate != null && columns.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        return firstExistingTable(connection, tableName) != null;
    }

    private String linkedTable(Role role, Connection connection) throws SQLException {
        return switch (role) {
            case STUDENT -> firstExistingTable(connection, "students");
            case PROFESSOR -> firstExistingTable(connection, "professors");
            case STAFF -> firstExistingTable(connection, "staffs", "staff");
            case ADMIN -> firstExistingTable(connection, "admins", "admin");
        };
    }

    private String linkedIdColumn(Role role) {
        return switch (role) {
            case STUDENT -> "student_id";
            case PROFESSOR -> "professor_id";
            case STAFF -> "staff_id";
            case ADMIN -> "admin_id";
        };
    }

    private Integer referenceId(Role role, User user) {
        return switch (role) {
            case STUDENT -> user.getStudentId();
            case PROFESSOR -> user.getProfessorId();
            case STAFF -> user.getStaffId();
            case ADMIN -> user.getAdminId();
        };
    }

    private String firstExistingTable(Connection connection, String... candidates) throws SQLException {
        Set<String> availableTables = availableTables(connection);
        for (String candidate : candidates) {
            if (availableTables.contains(candidate.toLowerCase(Locale.ROOT))) {
                return candidate;
            }
        }
        return null;
    }

    private Set<String> availableTables(Connection connection) throws SQLException {
        Set<String> tables = new LinkedHashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME").toLowerCase(Locale.ROOT));
            }
        }

        return tables;
    }

    private Set<String> tableColumns(Connection connection, String tableName) throws SQLException {
        Set<String> columns = new LinkedHashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, "%")) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
            }
        }
        return columns;
    }

    private Set<String> resultSetColumns(ResultSetMetaData metaData) throws SQLException {
        Set<String> columns = new LinkedHashSet<>();
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            columns.add(metaData.getColumnLabel(index).toLowerCase(Locale.ROOT));
        }
        return columns;
    }

    private String extractDisplayName(ResultSet resultSet, Set<String> columns) throws SQLException {
        if (columns.contains("full_name")) {
            return safeString(resultSet.getString("full_name"), null);
        }
        if (columns.contains("display_name")) {
            return safeString(resultSet.getString("display_name"), null);
        }
        if (columns.contains("name")) {
            return safeString(resultSet.getString("name"), null);
        }

        String firstName = columns.contains("first_name") ? safeString(resultSet.getString("first_name"), "") : "";
        String middleName = columns.contains("middle_name") ? safeString(resultSet.getString("middle_name"), "") : "";
        String lastName = columns.contains("last_name") ? safeString(resultSet.getString("last_name"), "") : "";
        String combined = (firstName + " " + middleName + " " + lastName).replaceAll("\\s+", " ").trim();

        if (!combined.isBlank()) {
            return combined;
        }

        if (columns.contains("username")) {
            return prettifyUsername(resultSet.getString("username"));
        }

        return null;
    }

    private boolean shouldSkipColumn(String column) {
        String normalized = column.toLowerCase(Locale.ROOT);
        return normalized.equals("password")
                || normalized.equals("full_name")
                || normalized.equals("display_name")
                || normalized.equals("username")
                || normalized.equals("email")
                || normalized.equals("role");
    }

    private String formatColumn(String column) {
        String[] parts = column.replace('_', ' ').split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT))
                    .append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    private String roleSectionTitle(String section) {
        return section == null || section.isBlank() ? "Dashboard" : section;
    }

    private String safeString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private Integer integerValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String prettifyUsername(String username) {
        if (username == null || username.isBlank()) {
            return "User";
        }

        String normalized = username.replace('.', ' ').replace('_', ' ').trim();
        if (normalized.matches("\\d+")) {
            return username;
        }

        String[] parts = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT))
                    .append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    private BigDecimal tryParseDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private int countMeaningfulRows(TableData tableData) {
        if (tableData.rows().isEmpty()) {
            return 0;
        }

        if (tableData.rows().size() == 1 && "No data available".equals(String.valueOf(tableData.rows().get(0)[0]))) {
            return 0;
        }

        return tableData.rows().size();
    }

    private record TableData(String[] columns, List<Object[]> rows) {
    }
}
