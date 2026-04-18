INSERT INTO departments (department_id, department_name, office_location)
VALUES (1, 'College of Engineering', 'Main Building');

INSERT INTO courses (course_id, course_code, course_name, department_id, total_units)
VALUES (1, 'BSIT', 'Bachelor of Science in Information Technology', 1, 144.00);

INSERT INTO school_years (school_year_id, school_year, is_current)
VALUES (1, '2025-2026', 1);

INSERT INTO semesters (semester_id, semester_name, is_current)
VALUES (1, '2nd Semester', 1);

INSERT INTO sections (section_id, section_name, course_id, section_year_level, status)
VALUES (1, 'BSIT 3A', 1, 3, 'open');

INSERT INTO students (student_id, student_number, first_name, middle_name, last_name, email, course_id, section_id, current_year_level, full_name)
VALUES
    (1, '2024-0001', 'Maria', 'Lopez', 'Cruz', 'maria.cruz@bulsu.edu.ph', 1, 1, 3, 'Maria Lopez Cruz'),
    (2, '2024-0002', 'John', 'Reyes', 'Santos', 'john.santos@bulsu.edu.ph', 1, 1, 3, 'John Reyes Santos');

INSERT INTO professors (professor_id, employee_number, first_name, middle_name, last_name, email, department_id, full_name)
VALUES (1, 'PROF-1001', 'Elena', 'M.', 'Bautista', 'elena.bautista@bulsu.edu.ph', 1, 'Elena M. Bautista');

INSERT INTO staffs (staff_id, employee_number, first_name, middle_name, last_name, email, department_id, full_name)
VALUES (1, 'STAFF-1001', 'Andrea', 'P.', 'Santos', 'andrea.santos@bulsu.edu.ph', 1, 'Andrea P. Santos');

INSERT INTO admins (admin_id, employee_number, first_name, middle_name, last_name, email, full_name)
VALUES (1, 'ADMIN-1001', 'Carlos', 'D.', 'Reyes', 'carlos.reyes@bulsu.edu.ph', 'Carlos D. Reyes');

INSERT INTO users (user_id, username, email, password, role, full_name, student_id, professor_id, admin_id, staff_id)
VALUES
    (1, '2024-0001', 'maria.cruz@bulsu.edu.ph', 'student123', 'student', 'Maria Lopez Cruz', 1, NULL, NULL, NULL),
    (2, 'john.santos', 'john.santos@bulsu.edu.ph', 'student123', 'student', 'John Reyes Santos', 2, NULL, NULL, NULL),
    (3, 'prof.bautista', 'elena.bautista@bulsu.edu.ph', 'prof123', 'professor', 'Elena M. Bautista', NULL, 1, NULL, NULL),
    (4, 'registrar.santos', 'andrea.santos@bulsu.edu.ph', 'staff123', 'staff', 'Andrea P. Santos', NULL, NULL, NULL, 1),
    (5, 'admin.bulsu', 'carlos.reyes@bulsu.edu.ph', 'admin123', 'admin', 'Carlos D. Reyes', NULL, NULL, 1, NULL);

INSERT INTO subjects (subject_id, subject_code, subject_name, units, department_id, professor_id, status)
VALUES
    (1, 'IT 301', 'Database Management Systems', 3.00, 1, 1, 'active'),
    (2, 'IT 302', 'Systems Analysis and Design', 3.00, 1, 1, 'active'),
    (3, 'IT 303', 'Computer Networks', 2.00, 1, 1, 'active');

INSERT INTO classes (class_id, subject_id, professor_id, section_id, semester_id, school_year_id, room, capacity, status)
VALUES
    (1, 1, 1, 1, 1, 1, 'Lab 201', 40, 'open'),
    (2, 2, 1, 1, 1, 1, 'Room 305', 40, 'open'),
    (3, 3, 1, 1, 1, 1, 'Net Lab', 35, 'open');

INSERT INTO class_schedules (class_schedule_id, class_id, day_of_week, start_time, end_time, room)
VALUES
    (1, 1, 'Monday', '09:00:00', '11:00:00', 'Lab 201'),
    (2, 2, 'Wednesday', '13:00:00', '15:00:00', 'Room 305'),
    (3, 3, 'Friday', '08:00:00', '10:00:00', 'Net Lab');

INSERT INTO enrollments (enrollment_id, student_id, class_id, enrollment_date, status)
VALUES
    (1, 1, 1, CURRENT_DATE, 'enrolled'),
    (2, 1, 2, CURRENT_DATE, 'enrolled'),
    (3, 1, 3, CURRENT_DATE, 'enrolled'),
    (4, 2, 1, CURRENT_DATE, 'enrolled'),
    (5, 2, 2, CURRENT_DATE, 'enrolled');

INSERT INTO grades (
    grade_id, enrollment_id,
    midterm_performance, midterm_attendance, midterm_written_works, midterm_exam,
    finals_performance, finals_attendance, finals_written_works, finals_exam,
    midterm_raw_score, finals_raw_score, final_raw_grade, final_grade, grade_value, remarks,
    midterm, finals
) VALUES
    (1, 1, 95.00, 92.00, 94.00, 96.00, 96.00, 95.00, 94.00, 97.00, 94.50, 95.60, 95.05, 1.25, 1.25, 'Passed', 94.50, 95.60),
    (2, 2, 88.00, 90.00, 89.00, 91.00, 90.00, 92.00, 91.00, 92.00, 89.40, 91.20, 90.30, 1.50, 1.50, 'Passed', 89.40, 91.20),
    (3, 4, 84.00, 86.00, 85.00, 87.00, 83.00, 85.00, 84.00, 86.00, 85.00, 84.20, 84.60, 2.00, 2.00, 'Passed', 85.00, 84.20);
