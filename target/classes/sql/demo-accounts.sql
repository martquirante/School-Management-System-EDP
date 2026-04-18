CREATE DATABASE IF NOT EXISTS school_management_system;
USE school_management_system;

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
