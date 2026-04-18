package com.mycompany.schoolmanagementssytem_edp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DatabaseBootstrap {

    private static volatile boolean initialized;

    private DatabaseBootstrap() {
    }

    public static synchronized void ensureInitialized() {
        if (initialized) {
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            createTables(connection);
            ensureColumns(connection);
            connection.commit();
            ensureDemoData(connection);
            initialized = true;
        } catch (SQLException exception) {
            initialized = false;
        }
    }

    private static void createTables(Connection connection) {
        List<String> statements = List.of(
                """
                CREATE TABLE IF NOT EXISTS departments (
                    department_id INT AUTO_INCREMENT PRIMARY KEY,
                    department_name VARCHAR(120) NOT NULL,
                    office_location VARCHAR(160) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS courses (
                    course_id INT AUTO_INCREMENT PRIMARY KEY,
                    course_code VARCHAR(40) NOT NULL,
                    course_name VARCHAR(160) NOT NULL,
                    department_id INT NULL,
                    total_units DECIMAL(6,2) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS school_years (
                    school_year_id INT AUTO_INCREMENT PRIMARY KEY,
                    school_year VARCHAR(40) NOT NULL,
                    is_current TINYINT(1) NOT NULL DEFAULT 1,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS semesters (
                    semester_id INT AUTO_INCREMENT PRIMARY KEY,
                    semester_name VARCHAR(60) NOT NULL,
                    is_current TINYINT(1) NOT NULL DEFAULT 1,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS sections (
                    section_id INT AUTO_INCREMENT PRIMARY KEY,
                    section_name VARCHAR(80) NOT NULL,
                    course_id INT NULL,
                    year_level INT NULL,
                    section_year_level INT NOT NULL DEFAULT 1,
                    status VARCHAR(20) NOT NULL DEFAULT 'open',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS students (
                    student_id INT AUTO_INCREMENT PRIMARY KEY,
                    student_number VARCHAR(40) NULL,
                    first_name VARCHAR(80) NOT NULL,
                    middle_name VARCHAR(80) NULL,
                    last_name VARCHAR(80) NOT NULL,
                    email VARCHAR(160) NULL,
                    course_id INT NULL,
                    year_level INT NULL,
                    contact_number VARCHAR(20) NULL,
                    section_id INT NULL,
                    current_year_level INT NOT NULL DEFAULT 1,
                    full_name VARCHAR(180) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS professors (
                    professor_id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_number VARCHAR(40) NULL,
                    first_name VARCHAR(80) NOT NULL,
                    middle_name VARCHAR(80) NULL,
                    last_name VARCHAR(80) NOT NULL,
                    email VARCHAR(160) NULL,
                    department_id INT NULL,
                    contact_number VARCHAR(20) NULL,
                    full_name VARCHAR(180) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS staffs (
                    staff_id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_number VARCHAR(40) NULL,
                    first_name VARCHAR(80) NOT NULL,
                    middle_name VARCHAR(80) NULL,
                    last_name VARCHAR(80) NOT NULL,
                    email VARCHAR(160) NULL,
                    department_id INT NULL,
                    full_name VARCHAR(180) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS admins (
                    admin_id INT AUTO_INCREMENT PRIMARY KEY,
                    employee_number VARCHAR(40) NULL,
                    first_name VARCHAR(80) NOT NULL,
                    middle_name VARCHAR(80) NULL,
                    last_name VARCHAR(80) NOT NULL,
                    email VARCHAR(160) NULL,
                    full_name VARCHAR(180) NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(80) NOT NULL,
                    email VARCHAR(160) NOT NULL,
                    password VARCHAR(160) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    full_name VARCHAR(180) NULL,
                    student_id INT NULL,
                    professor_id INT NULL,
                    admin_id INT NULL,
                    staff_id INT NULL,
                    is_active TINYINT(1) NOT NULL DEFAULT 1,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS subjects (
                    subject_id INT AUTO_INCREMENT PRIMARY KEY,
                    subject_code VARCHAR(40) NOT NULL,
                    subject_name VARCHAR(180) NOT NULL,
                    units DECIMAL(6,2) NOT NULL DEFAULT 3.00,
                    department_id INT NULL,
                    professor_id INT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'active',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS classes (
                    class_id INT AUTO_INCREMENT PRIMARY KEY,
                    subject_id INT NOT NULL,
                    professor_id INT NULL,
                    section_id INT NULL,
                    semester_id INT NULL,
                    school_year_id INT NULL,
                    room VARCHAR(50) NULL,
                    capacity INT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'open',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS class_schedules (
                    class_schedule_id INT AUTO_INCREMENT PRIMARY KEY,
                    class_id INT NOT NULL,
                    day_of_week VARCHAR(20) NOT NULL,
                    start_time TIME NOT NULL,
                    end_time TIME NOT NULL,
                    room VARCHAR(50) NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS enrollments (
                    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id INT NOT NULL,
                    class_id INT NOT NULL,
                    enrollment_date DATE NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'enrolled',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS grades (
                    grade_id INT AUTO_INCREMENT PRIMARY KEY,
                    enrollment_id INT NOT NULL,
                    midterm_performance DECIMAL(6,2) NULL,
                    midterm_attendance DECIMAL(6,2) NULL,
                    midterm_written_works DECIMAL(6,2) NULL,
                    midterm_exam DECIMAL(6,2) NULL,
                    finals_performance DECIMAL(6,2) NULL,
                    finals_attendance DECIMAL(6,2) NULL,
                    finals_written_works DECIMAL(6,2) NULL,
                    finals_exam DECIMAL(6,2) NULL,
                    midterm_raw_score DECIMAL(6,2) NULL,
                    finals_raw_score DECIMAL(6,2) NULL,
                    final_raw_grade DECIMAL(6,2) NULL,
                    final_grade DECIMAL(6,2) NULL,
                    grade_value DECIMAL(6,2) NULL,
                    remarks VARCHAR(40) NULL,
                    prelim DECIMAL(6,2) NULL,
                    midterm DECIMAL(6,2) NULL,
                    finals DECIMAL(6,2) NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """
        );

        for (String statement : statements) {
            executeSilently(connection, statement);
        }
    }

    private static void ensureColumns(Connection connection) {
        List<String> statements = List.of(
                "ALTER TABLE departments ADD COLUMN IF NOT EXISTS office_location VARCHAR(160) NULL",
                "ALTER TABLE departments ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE courses ADD COLUMN IF NOT EXISTS course_code VARCHAR(40) NULL",
                "ALTER TABLE courses ADD COLUMN IF NOT EXISTS total_units DECIMAL(6,2) NULL",
                "ALTER TABLE courses ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE school_years ADD COLUMN IF NOT EXISTS is_current TINYINT(1) NOT NULL DEFAULT 1",
                "ALTER TABLE school_years ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE semesters ADD COLUMN IF NOT EXISTS is_current TINYINT(1) NOT NULL DEFAULT 1",
                "ALTER TABLE semesters ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE sections ADD COLUMN IF NOT EXISTS year_level INT NULL",
                "ALTER TABLE sections ADD COLUMN IF NOT EXISTS section_year_level INT NOT NULL DEFAULT 1",
                "ALTER TABLE sections ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'open'",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS course_id INT NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS year_level INT NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS contact_number VARCHAR(20) NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS section_id INT NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS current_year_level INT NOT NULL DEFAULT 1",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS student_number VARCHAR(40) NULL",
                "ALTER TABLE students ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE professors ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL",
                "ALTER TABLE professors ADD COLUMN IF NOT EXISTS contact_number VARCHAR(20) NULL",
                "ALTER TABLE professors ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL",
                "ALTER TABLE professors ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS first_name VARCHAR(80) NULL",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS middle_name VARCHAR(80) NULL",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS last_name VARCHAR(80) NULL",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL",
                "ALTER TABLE staffs ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS first_name VARCHAR(80) NULL",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS middle_name VARCHAR(80) NULL",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS last_name VARCHAR(80) NULL",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL",
                "ALTER TABLE admins ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS student_id INT NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS professor_id INT NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS admin_id INT NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS staff_id INT NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active TINYINT(1) NOT NULL DEFAULT 1",
                "ALTER TABLE subjects ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active'",
                "ALTER TABLE subjects ADD COLUMN IF NOT EXISTS professor_id INT NULL",
                "ALTER TABLE classes ADD COLUMN IF NOT EXISTS semester_id INT NULL",
                "ALTER TABLE classes ADD COLUMN IF NOT EXISTS school_year_id INT NULL",
                "ALTER TABLE classes ADD COLUMN IF NOT EXISTS capacity INT NULL",
                "ALTER TABLE classes ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'open'",
                "ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS enrollment_date DATE NULL",
                "ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'enrolled'",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_performance DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_attendance DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_written_works DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_exam DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_performance DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_attendance DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_written_works DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_exam DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_raw_score DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_raw_score DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS final_raw_grade DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS final_grade DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS grade_value DECIMAL(6,2) NULL",
                "ALTER TABLE grades ADD COLUMN IF NOT EXISTS remarks VARCHAR(40) NULL"
        );

        for (String statement : statements) {
            executeSilently(connection, statement);
        }
    }

    private static void ensureDemoData(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        deleteDemoData(connection);
        insertDemoData(connection);
        connection.commit();
    }

    private static void deleteDemoData(Connection connection) {
        executeSilently(connection, "DELETE FROM grades WHERE enrollment_id IN (9701, 9702, 9703, 9704, 9705) OR grade_id IN (9801, 9802, 9803)");
        executeSilently(connection, "DELETE FROM class_schedules WHERE class_schedule_id IN (9651, 9652, 9653) OR class_id IN (9601, 9602, 9603)");
        executeSilently(connection, "DELETE FROM enrollments WHERE enrollment_id IN (9701, 9702, 9703, 9704, 9705) OR student_id IN (9001, 9002) OR class_id IN (9601, 9602, 9603)");
        executeSilently(connection, "DELETE FROM classes WHERE class_id IN (9601, 9602, 9603)");
        executeSilently(connection, "DELETE FROM subjects WHERE subject_id IN (9501, 9502, 9503)");
        executeSilently(connection, "DELETE FROM users WHERE username IN ('admin.bulsu', 'registrar.santos', 'prof.bautista', '2024-0001', 'john.santos') OR user_id IN (90001, 90002, 90003, 90004, 90005)");
        executeSilently(connection, "DELETE FROM students WHERE student_id IN (9001, 9002) OR student_number IN ('2024-0001', '2024-0002') OR email IN ('maria.cruz@bulsu.edu.ph', 'john.santos@bulsu.edu.ph')");
        executeSilently(connection, "DELETE FROM professors WHERE professor_id IN (9101) OR employee_number = 'PROF-1001' OR email = 'elena.bautista@bulsu.edu.ph'");
        executeSilently(connection, "DELETE FROM staffs WHERE staff_id IN (9201) OR employee_number = 'STAFF-1001' OR email = 'andrea.santos@bulsu.edu.ph'");
        executeSilently(connection, "DELETE FROM admins WHERE admin_id IN (9301) OR employee_number = 'ADMIN-1001' OR email = 'carlos.reyes@bulsu.edu.ph'");
        executeSilently(connection, "DELETE FROM sections WHERE section_id IN (9403)");
        executeSilently(connection, "DELETE FROM courses WHERE course_id IN (9402)");
        executeSilently(connection, "DELETE FROM departments WHERE department_id IN (9401)");
        executeSilently(connection, "DELETE FROM semesters WHERE semester_id IN (9404)");
        executeSilently(connection, "DELETE FROM school_years WHERE school_year_id IN (9405)");
    }

    private static void insertDemoData(Connection connection) {
        insertSilently(connection, "departments", row(
                "department_id", 9401,
                "department_name", "College of Engineering",
                "office_location", "Main Building",
                "status", "active"
        ));
        insertSilently(connection, "courses", row(
                "course_id", 9402,
                "course_code", "BSIT",
                "course_name", "Bachelor of Science in Information Technology",
                "department_id", 9401,
                "total_units", 144.00,
                "status", "active"
        ));
        insertSilently(connection, "school_years", row(
                "school_year_id", 9405,
                "school_year", "2025-2026",
                "is_current", 1,
                "status", "active"
        ));
        insertSilently(connection, "semesters", row(
                "semester_id", 9404,
                "semester_name", "2nd Semester",
                "is_current", 1,
                "status", "active"
        ));
        insertSilently(connection, "sections", row(
                "section_id", 9403,
                "section_name", "BSIT 3A",
                "course_id", 9402,
                "section_year_level", 3,
                "year_level", 3,
                "status", "open"
        ));

        insertSilently(connection, "students", row(
                "student_id", 9001,
                "student_number", "2024-0001",
                "first_name", "Maria",
                "middle_name", "Lopez",
                "last_name", "Cruz",
                "email", "maria.cruz@bulsu.edu.ph",
                "course_id", 9402,
                "section_id", 9403,
                "current_year_level", 3,
                "year_level", 3,
                "contact_number", "09171234567",
                "full_name", "Maria Lopez Cruz",
                "status", "active"
        ));
        insertSilently(connection, "students", row(
                "student_id", 9002,
                "student_number", "2024-0002",
                "first_name", "John",
                "middle_name", "Reyes",
                "last_name", "Santos",
                "email", "john.santos@bulsu.edu.ph",
                "course_id", 9402,
                "section_id", 9403,
                "current_year_level", 3,
                "year_level", 3,
                "contact_number", "09179876543",
                "full_name", "John Reyes Santos",
                "status", "active"
        ));
        insertSilently(connection, "professors", row(
                "professor_id", 9101,
                "employee_number", "PROF-1001",
                "first_name", "Elena",
                "middle_name", "M.",
                "last_name", "Bautista",
                "email", "elena.bautista@bulsu.edu.ph",
                "department_id", 9401,
                "contact_number", "09170001111",
                "full_name", "Elena M. Bautista",
                "status", "active"
        ));
        insertSilently(connection, "staffs", row(
                "staff_id", 9201,
                "employee_number", "STAFF-1001",
                "first_name", "Andrea",
                "middle_name", "P.",
                "last_name", "Santos",
                "email", "andrea.santos@bulsu.edu.ph",
                "department_id", 9401,
                "full_name", "Andrea P. Santos",
                "status", "active"
        ));
        insertSilently(connection, "admins", row(
                "admin_id", 9301,
                "employee_number", "ADMIN-1001",
                "first_name", "Carlos",
                "middle_name", "D.",
                "last_name", "Reyes",
                "email", "carlos.reyes@bulsu.edu.ph",
                "full_name", "Carlos D. Reyes",
                "status", "active"
        ));

        insertSilently(connection, "users", row(
                "user_id", 90001,
                "username", "2024-0001",
                "email", "maria.cruz@bulsu.edu.ph",
                "password", "student123",
                "role", "student",
                "full_name", "Maria Lopez Cruz",
                "student_id", 9001,
                "is_active", 1
        ));
        insertSilently(connection, "users", row(
                "user_id", 90002,
                "username", "john.santos",
                "email", "john.santos@bulsu.edu.ph",
                "password", "student123",
                "role", "student",
                "full_name", "John Reyes Santos",
                "student_id", 9002,
                "is_active", 1
        ));
        insertSilently(connection, "users", row(
                "user_id", 90003,
                "username", "prof.bautista",
                "email", "elena.bautista@bulsu.edu.ph",
                "password", "prof123",
                "role", "professor",
                "full_name", "Elena M. Bautista",
                "professor_id", 9101,
                "is_active", 1
        ));
        insertSilently(connection, "users", row(
                "user_id", 90004,
                "username", "registrar.santos",
                "email", "andrea.santos@bulsu.edu.ph",
                "password", "staff123",
                "role", "staff",
                "full_name", "Andrea P. Santos",
                "staff_id", 9201,
                "is_active", 1
        ));
        insertSilently(connection, "users", row(
                "user_id", 90005,
                "username", "admin.bulsu",
                "email", "carlos.reyes@bulsu.edu.ph",
                "password", "admin123",
                "role", "admin",
                "full_name", "Carlos D. Reyes",
                "admin_id", 9301,
                "is_active", 1
        ));

        insertSilently(connection, "subjects", row(
                "subject_id", 9501,
                "subject_code", "IT 301",
                "subject_name", "Database Management Systems",
                "units", 3,
                "department_id", 9401,
                "professor_id", 9101,
                "status", "active"
        ));
        insertSilently(connection, "subjects", row(
                "subject_id", 9502,
                "subject_code", "IT 302",
                "subject_name", "Systems Analysis and Design",
                "units", 3,
                "department_id", 9401,
                "professor_id", 9101,
                "status", "active"
        ));
        insertSilently(connection, "subjects", row(
                "subject_id", 9503,
                "subject_code", "IT 303",
                "subject_name", "Computer Networks",
                "units", 2,
                "department_id", 9401,
                "professor_id", 9101,
                "status", "active"
        ));

        insertSilently(connection, "classes", row(
                "class_id", 9601,
                "subject_id", 9501,
                "professor_id", 9101,
                "section_id", 9403,
                "semester_id", 9404,
                "school_year_id", 9405,
                "schedule_info", "Monday 09:00-11:00 Lab 201",
                "room", "Lab 201",
                "capacity", 40,
                "status", "open"
        ));
        insertSilently(connection, "classes", row(
                "class_id", 9602,
                "subject_id", 9502,
                "professor_id", 9101,
                "section_id", 9403,
                "semester_id", 9404,
                "school_year_id", 9405,
                "schedule_info", "Wednesday 13:00-15:00 Room 305",
                "room", "Room 305",
                "capacity", 40,
                "status", "open"
        ));
        insertSilently(connection, "classes", row(
                "class_id", 9603,
                "subject_id", 9503,
                "professor_id", 9101,
                "section_id", 9403,
                "semester_id", 9404,
                "school_year_id", 9405,
                "schedule_info", "Friday 08:00-10:00 Net Lab",
                "room", "Net Lab",
                "capacity", 35,
                "status", "open"
        ));

        insertSilently(connection, "class_schedules", row(
                "class_schedule_id", 9651,
                "class_id", 9601,
                "day_of_week", "Monday",
                "start_time", "09:00:00",
                "end_time", "11:00:00",
                "room", "Lab 201"
        ));
        insertSilently(connection, "class_schedules", row(
                "class_schedule_id", 9652,
                "class_id", 9602,
                "day_of_week", "Wednesday",
                "start_time", "13:00:00",
                "end_time", "15:00:00",
                "room", "Room 305"
        ));
        insertSilently(connection, "class_schedules", row(
                "class_schedule_id", 9653,
                "class_id", 9603,
                "day_of_week", "Friday",
                "start_time", "08:00:00",
                "end_time", "10:00:00",
                "room", "Net Lab"
        ));

        insertSilently(connection, "enrollments", row(
                "enrollment_id", 9701,
                "student_id", 9001,
                "class_id", 9601,
                "enrollment_date", java.sql.Date.valueOf(java.time.LocalDate.now()),
                "status", "enrolled"
        ));
        insertSilently(connection, "enrollments", row(
                "enrollment_id", 9702,
                "student_id", 9001,
                "class_id", 9602,
                "enrollment_date", java.sql.Date.valueOf(java.time.LocalDate.now()),
                "status", "enrolled"
        ));
        insertSilently(connection, "enrollments", row(
                "enrollment_id", 9703,
                "student_id", 9001,
                "class_id", 9603,
                "enrollment_date", java.sql.Date.valueOf(java.time.LocalDate.now()),
                "status", "enrolled"
        ));
        insertSilently(connection, "enrollments", row(
                "enrollment_id", 9704,
                "student_id", 9002,
                "class_id", 9601,
                "enrollment_date", java.sql.Date.valueOf(java.time.LocalDate.now()),
                "status", "enrolled"
        ));
        insertSilently(connection, "enrollments", row(
                "enrollment_id", 9705,
                "student_id", 9002,
                "class_id", 9602,
                "enrollment_date", java.sql.Date.valueOf(java.time.LocalDate.now()),
                "status", "enrolled"
        ));

        insertSilently(connection, "grades", row(
                "grade_id", 9801,
                "enrollment_id", 9701,
                "midterm_performance", 95.00,
                "midterm_attendance", 92.00,
                "midterm_written_works", 94.00,
                "midterm_exam", 96.00,
                "finals_performance", 96.00,
                "finals_attendance", 95.00,
                "finals_written_works", 94.00,
                "finals_exam", 97.00,
                "midterm_raw_score", 94.50,
                "finals_raw_score", 95.60,
                "final_raw_grade", 95.05,
                "final_grade", 1.25,
                "grade_value", 1.25,
                "remarks", "Passed",
                "midterm", 94.50,
                "finals", 95.60
        ));
        insertSilently(connection, "grades", row(
                "grade_id", 9802,
                "enrollment_id", 9702,
                "midterm_performance", 88.00,
                "midterm_attendance", 90.00,
                "midterm_written_works", 89.00,
                "midterm_exam", 91.00,
                "finals_performance", 90.00,
                "finals_attendance", 92.00,
                "finals_written_works", 91.00,
                "finals_exam", 92.00,
                "midterm_raw_score", 89.40,
                "finals_raw_score", 91.20,
                "final_raw_grade", 90.30,
                "final_grade", 1.50,
                "grade_value", 1.50,
                "remarks", "Passed",
                "midterm", 89.40,
                "finals", 91.20
        ));
        insertSilently(connection, "grades", row(
                "grade_id", 9803,
                "enrollment_id", 9704,
                "midterm_performance", 84.00,
                "midterm_attendance", 86.00,
                "midterm_written_works", 85.00,
                "midterm_exam", 87.00,
                "finals_performance", 83.00,
                "finals_attendance", 85.00,
                "finals_written_works", 84.00,
                "finals_exam", 86.00,
                "midterm_raw_score", 85.00,
                "finals_raw_score", 84.20,
                "final_raw_grade", 84.60,
                "final_grade", 2.00,
                "grade_value", 2.00,
                "remarks", "Passed",
                "midterm", 85.00,
                "finals", 84.20
        ));
    }

    private static Map<String, Object> row(Object... values) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            row.put(String.valueOf(values[index]), values[index + 1]);
        }
        return row;
    }

    private static void insertSilently(Connection connection, String tableName, Map<String, Object> values) {
        try {
            Set<String> columns = tableColumns(connection, tableName);
            if (columns.isEmpty()) {
                return;
            }

            List<String> insertColumns = new java.util.ArrayList<>();
            List<Object> insertValues = new java.util.ArrayList<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String normalizedColumn = entry.getKey().toLowerCase(Locale.ROOT);
                if (!columns.contains(normalizedColumn)) {
                    continue;
                }
                insertColumns.add(entry.getKey());
                insertValues.add(entry.getValue());
            }

            if (insertColumns.isEmpty()) {
                return;
            }

            String placeholders = String.join(", ", insertColumns.stream().map(column -> "?").toList());
            String sql = "INSERT INTO " + tableName + " (" + String.join(", ", insertColumns) + ") VALUES (" + placeholders + ")";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int index = 0; index < insertValues.size(); index++) {
                    statement.setObject(index + 1, insertValues.get(index));
                }
                statement.executeUpdate();
            }
        } catch (SQLException ignored) {
            // Best effort demo seed for mixed legacy schemas.
        }
    }

    private static Set<String> tableColumns(Connection connection, String tableName) throws SQLException {
        Set<String> columns = new LinkedHashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), null, tableName, "%")) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
            }
        }
        return columns;
    }

    private static void executeSilently(Connection connection, String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // Best effort bootstrap to keep the app usable even on partial schemas.
        }
    }
}
