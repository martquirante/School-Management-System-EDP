CREATE DATABASE IF NOT EXISTS school_management_system;
USE school_management_system;

CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(120) NOT NULL,
    office_location VARCHAR(160) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(40) NOT NULL,
    course_name VARCHAR(160) NOT NULL,
    department_id INT NULL,
    total_units DECIMAL(6,2) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS school_years (
    school_year_id INT AUTO_INCREMENT PRIMARY KEY,
    school_year VARCHAR(40) NOT NULL,
    is_current TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS semesters (
    semester_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_name VARCHAR(60) NOT NULL,
    is_current TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    section_name VARCHAR(80) NOT NULL,
    course_id INT NULL,
    year_level INT NULL,
    section_year_level INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'open',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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
);

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
);

CREATE TABLE IF NOT EXISTS staffs (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_number VARCHAR(40) NULL,
    first_name VARCHAR(80) NULL,
    middle_name VARCHAR(80) NULL,
    last_name VARCHAR(80) NULL,
    email VARCHAR(160) NULL,
    department_id INT NULL,
    full_name VARCHAR(180) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_number VARCHAR(40) NULL,
    first_name VARCHAR(80) NULL,
    middle_name VARCHAR(80) NULL,
    last_name VARCHAR(80) NULL,
    email VARCHAR(160) NULL,
    full_name VARCHAR(180) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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
);

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
);

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
);

CREATE TABLE IF NOT EXISTS class_schedules (
    class_schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    enrollment_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'enrolled',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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
);

ALTER TABLE departments ADD COLUMN IF NOT EXISTS office_location VARCHAR(160) NULL;
ALTER TABLE departments ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE courses ADD COLUMN IF NOT EXISTS course_code VARCHAR(40) NULL;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS total_units DECIMAL(6,2) NULL;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE school_years ADD COLUMN IF NOT EXISTS is_current TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE school_years ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE semesters ADD COLUMN IF NOT EXISTS is_current TINYINT(1) NOT NULL DEFAULT 1;
ALTER TABLE semesters ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE sections ADD COLUMN IF NOT EXISTS year_level INT NULL;
ALTER TABLE sections ADD COLUMN IF NOT EXISTS section_year_level INT NOT NULL DEFAULT 1;
ALTER TABLE sections ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'open';

ALTER TABLE students ADD COLUMN IF NOT EXISTS student_number VARCHAR(40) NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS course_id INT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS year_level INT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS contact_number VARCHAR(20) NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS section_id INT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS current_year_level INT NOT NULL DEFAULT 1;
ALTER TABLE students ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE professors ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL;
ALTER TABLE professors ADD COLUMN IF NOT EXISTS contact_number VARCHAR(20) NULL;
ALTER TABLE professors ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL;
ALTER TABLE professors ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE staffs ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL;
ALTER TABLE staffs ADD COLUMN IF NOT EXISTS first_name VARCHAR(80) NULL;
ALTER TABLE staffs ADD COLUMN IF NOT EXISTS middle_name VARCHAR(80) NULL;
ALTER TABLE staffs ADD COLUMN IF NOT EXISTS last_name VARCHAR(80) NULL;
ALTER TABLE staffs ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL;
ALTER TABLE staffs ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE admins ADD COLUMN IF NOT EXISTS employee_number VARCHAR(40) NULL;
ALTER TABLE admins ADD COLUMN IF NOT EXISTS first_name VARCHAR(80) NULL;
ALTER TABLE admins ADD COLUMN IF NOT EXISTS middle_name VARCHAR(80) NULL;
ALTER TABLE admins ADD COLUMN IF NOT EXISTS last_name VARCHAR(80) NULL;
ALTER TABLE admins ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL;
ALTER TABLE admins ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(180) NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS student_id INT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS professor_id INT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS admin_id INT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS staff_id INT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active TINYINT(1) NOT NULL DEFAULT 1;

ALTER TABLE subjects ADD COLUMN IF NOT EXISTS professor_id INT NULL;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'active';

ALTER TABLE classes ADD COLUMN IF NOT EXISTS semester_id INT NULL;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS school_year_id INT NULL;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS capacity INT NULL;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'open';

ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS enrollment_date DATE NULL;
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'enrolled';

ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_performance DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_attendance DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_written_works DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_exam DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_performance DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_attendance DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_written_works DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_exam DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS midterm_raw_score DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS finals_raw_score DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS final_raw_grade DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS final_grade DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS grade_value DECIMAL(6,2) NULL;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS remarks VARCHAR(40) NULL;

START TRANSACTION;

DELETE FROM grades
WHERE enrollment_id IN (9701, 9702, 9703, 9704, 9705)
   OR grade_id IN (9801, 9802, 9803);

DELETE FROM class_schedules
WHERE class_schedule_id IN (9651, 9652, 9653)
   OR class_id IN (9601, 9602, 9603);

DELETE FROM enrollments
WHERE enrollment_id IN (9701, 9702, 9703, 9704, 9705)
   OR student_id IN (9001, 9002)
   OR class_id IN (9601, 9602, 9603);

DELETE FROM classes
WHERE class_id IN (9601, 9602, 9603);

DELETE FROM subjects
WHERE subject_id IN (9501, 9502, 9503);

DELETE FROM users
WHERE username IN ('admin.bulsu', 'registrar.santos', 'prof.bautista', '2024-0001', 'john.santos')
   OR user_id IN (90001, 90002, 90003, 90004, 90005);

DELETE FROM students
WHERE student_id IN (9001, 9002)
   OR student_number IN ('2024-0001', '2024-0002')
   OR email IN ('maria.cruz@bulsu.edu.ph', 'john.santos@bulsu.edu.ph');

DELETE FROM professors
WHERE professor_id IN (9101)
   OR employee_number = 'PROF-1001'
   OR email = 'elena.bautista@bulsu.edu.ph';

DELETE FROM staffs
WHERE staff_id IN (9201)
   OR employee_number = 'STAFF-1001'
   OR email = 'andrea.santos@bulsu.edu.ph';

DELETE FROM admins
WHERE admin_id IN (9301)
   OR employee_number = 'ADMIN-1001'
   OR email = 'carlos.reyes@bulsu.edu.ph';

DELETE FROM sections
WHERE section_id IN (9403);

DELETE FROM courses
WHERE course_id IN (9402);

DELETE FROM departments
WHERE department_id IN (9401);

DELETE FROM semesters
WHERE semester_id IN (9404);

DELETE FROM school_years
WHERE school_year_id IN (9405);

INSERT INTO departments (
    department_id, department_name, office_location, status
) VALUES (
    9401, 'College of Engineering', 'Main Building', 'active'
);

INSERT INTO courses (
    course_id, course_code, course_name, department_id, total_units, status
) VALUES (
    9402, 'BSIT', 'Bachelor of Science in Information Technology', 9401, 144.00, 'active'
);

INSERT INTO school_years (
    school_year_id, school_year, is_current, status
) VALUES (
    9405, '2025-2026', 1, 'active'
);

INSERT INTO semesters (
    semester_id, semester_name, is_current, status
) VALUES (
    9404, '2nd Semester', 1, 'active'
);

INSERT INTO sections (
    section_id, section_name, course_id, year_level, section_year_level, status
) VALUES (
    9403, 'BSIT 3A', 9402, 3, 3, 'open'
);

INSERT INTO students (
    student_id, student_number, first_name, middle_name, last_name, email,
    course_id, year_level, contact_number, section_id, current_year_level, full_name, status
) VALUES
(
    9001, '2024-0001', 'Maria', 'Lopez', 'Cruz', 'maria.cruz@bulsu.edu.ph',
    9402, 3, '09171234567', 9403, 3, 'Maria Lopez Cruz', 'active'
),
(
    9002, '2024-0002', 'John', 'Reyes', 'Santos', 'john.santos@bulsu.edu.ph',
    9402, 3, '09179876543', 9403, 3, 'John Reyes Santos', 'active'
);

INSERT INTO professors (
    professor_id, employee_number, first_name, middle_name, last_name, email, contact_number,
    department_id, full_name, status
) VALUES (
    9101, 'PROF-1001', 'Elena', 'M.', 'Bautista', 'elena.bautista@bulsu.edu.ph', '09170001111',
    9401, 'Elena M. Bautista', 'active'
);

INSERT INTO staffs (
    staff_id, employee_number, first_name, middle_name, last_name, email,
    department_id, full_name, status
) VALUES (
    9201, 'STAFF-1001', 'Andrea', 'P.', 'Santos', 'andrea.santos@bulsu.edu.ph',
    9401, 'Andrea P. Santos', 'active'
);

INSERT INTO admins (
    admin_id, employee_number, first_name, middle_name, last_name, email,
    full_name, status
) VALUES (
    9301, 'ADMIN-1001', 'Carlos', 'D.', 'Reyes', 'carlos.reyes@bulsu.edu.ph',
    'Carlos D. Reyes', 'active'
);

INSERT INTO users (
    user_id, username, email, password, role, full_name,
    student_id, professor_id, admin_id, staff_id, is_active
) VALUES
(
    90001, '2024-0001', 'maria.cruz@bulsu.edu.ph', 'student123', 'student', 'Maria Lopez Cruz',
    9001, NULL, NULL, NULL, 1
),
(
    90002, 'john.santos', 'john.santos@bulsu.edu.ph', 'student123', 'student', 'John Reyes Santos',
    9002, NULL, NULL, NULL, 1
),
(
    90003, 'prof.bautista', 'elena.bautista@bulsu.edu.ph', 'prof123', 'professor', 'Elena M. Bautista',
    NULL, 9101, NULL, NULL, 1
),
(
    90004, 'registrar.santos', 'andrea.santos@bulsu.edu.ph', 'staff123', 'staff', 'Andrea P. Santos',
    NULL, NULL, NULL, 9201, 1
),
(
    90005, 'admin.bulsu', 'carlos.reyes@bulsu.edu.ph', 'admin123', 'admin', 'Carlos D. Reyes',
    NULL, NULL, 9301, NULL, 1
);

INSERT INTO subjects (
    subject_id, subject_code, subject_name, units, department_id, professor_id, status
) VALUES
(
    9501, 'IT 301', 'Database Management Systems', 3.00, 9401, 9101, 'active'
),
(
    9502, 'IT 302', 'Systems Analysis and Design', 3.00, 9401, 9101, 'active'
),
(
    9503, 'IT 303', 'Computer Networks', 2.00, 9401, 9101, 'active'
);

INSERT INTO classes (
    class_id, subject_id, professor_id, section_id, semester_id, school_year_id,
    room, capacity, status
) VALUES
(
    9601, 9501, 9101, 9403, 9404, 9405, 'Lab 201', 40, 'open'
),
(
    9602, 9502, 9101, 9403, 9404, 9405, 'Room 305', 40, 'open'
),
(
    9603, 9503, 9101, 9403, 9404, 9405, 'Net Lab', 35, 'open'
);

INSERT INTO class_schedules (
    class_schedule_id, class_id, day_of_week, start_time, end_time, room
) VALUES
(
    9651, 9601, 'Monday', '09:00:00', '11:00:00', 'Lab 201'
),
(
    9652, 9602, 'Wednesday', '13:00:00', '15:00:00', 'Room 305'
),
(
    9653, 9603, 'Friday', '08:00:00', '10:00:00', 'Net Lab'
);

INSERT INTO enrollments (
    enrollment_id, student_id, class_id, enrollment_date, status
) VALUES
(
    9701, 9001, 9601, CURRENT_DATE, 'enrolled'
),
(
    9702, 9001, 9602, CURRENT_DATE, 'enrolled'
),
(
    9703, 9001, 9603, CURRENT_DATE, 'enrolled'
),
(
    9704, 9002, 9601, CURRENT_DATE, 'enrolled'
),
(
    9705, 9002, 9602, CURRENT_DATE, 'enrolled'
);

INSERT INTO grades (
    grade_id, enrollment_id,
    midterm_performance, midterm_attendance, midterm_written_works, midterm_exam,
    finals_performance, finals_attendance, finals_written_works, finals_exam,
    midterm_raw_score, finals_raw_score, final_raw_grade,
    final_grade, grade_value, remarks, midterm, finals
) VALUES
(
    9801, 9701,
    95.00, 92.00, 94.00, 96.00,
    96.00, 95.00, 94.00, 97.00,
    94.50, 95.60, 95.05,
    1.25, 1.25, 'Passed', 94.50, 95.60
),
(
    9802, 9702,
    88.00, 90.00, 89.00, 91.00,
    90.00, 92.00, 91.00, 92.00,
    89.40, 91.20, 90.30,
    1.50, 1.50, 'Passed', 89.40, 91.20
),
(
    9803, 9704,
    84.00, 86.00, 85.00, 87.00,
    83.00, 85.00, 84.00, 86.00,
    85.00, 84.20, 84.60,
    2.00, 2.00, 'Passed', 85.00, 84.20
);

COMMIT;
