START TRANSACTION;

-- =========================================================
-- 1) REMOVE AMBIGUOUS YEAR LEVEL NAMING
-- =========================================================

ALTER TABLE students
    CHANGE COLUMN year_level current_year_level INT NOT NULL;

ALTER TABLE sections
    CHANGE COLUMN year_level section_year_level INT NOT NULL;

-- =========================================================
-- 2) ADD STATUS / TIMESTAMPS FOR BETTER TRACKING
-- =========================================================

ALTER TABLE students
    ADD COLUMN status ENUM('active','inactive','graduated','dropped','transferred') NOT NULL DEFAULT 'active' AFTER email,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE professors
    ADD COLUMN status ENUM('active','inactive','retired') NOT NULL DEFAULT 'active' AFTER email,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE staffs
    ADD COLUMN status ENUM('active','inactive') NOT NULL DEFAULT 'active' AFTER department_id,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE admins
    ADD COLUMN status ENUM('active','inactive') NOT NULL DEFAULT 'active' AFTER email,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE subjects
    ADD COLUMN status ENUM('active','inactive') NOT NULL DEFAULT 'active' AFTER department_id,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE courses
    ADD COLUMN status ENUM('active','inactive') NOT NULL DEFAULT 'active' AFTER department_id,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE sections
    ADD COLUMN status ENUM('open','closed','archived') NOT NULL DEFAULT 'open' AFTER section_year_level,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE classes
    ADD COLUMN capacity INT NULL AFTER room,
    ADD COLUMN status ENUM('open','closed','cancelled','completed') NOT NULL DEFAULT 'open' AFTER capacity,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE enrollments
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE grades
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE users
    ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER role,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- =========================================================
-- 3) ADD UNIQUE CONSTRAINTS
-- =========================================================

ALTER TABLE departments
    ADD CONSTRAINT uq_departments_department_name UNIQUE (department_name);

ALTER TABLE courses
    ADD CONSTRAINT uq_courses_course_name UNIQUE (course_name);

ALTER TABLE school_years
    ADD CONSTRAINT uq_school_years_school_year UNIQUE (school_year);

ALTER TABLE semesters
    ADD CONSTRAINT uq_semesters_semester_name UNIQUE (semester_name);

ALTER TABLE subjects
    ADD CONSTRAINT uq_subjects_subject_code UNIQUE (subject_code);

ALTER TABLE sections
    ADD CONSTRAINT uq_sections_section_name UNIQUE (section_name);

ALTER TABLE admins
    ADD CONSTRAINT uq_admins_email UNIQUE (email);

ALTER TABLE staffs
    ADD CONSTRAINT uq_staffs_email UNIQUE (email);

ALTER TABLE students
    ADD CONSTRAINT uq_students_email UNIQUE (email);

ALTER TABLE professors
    ADD CONSTRAINT uq_professors_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uq_users_username UNIQUE (username),
    ADD CONSTRAINT uq_users_email UNIQUE (email),
    ADD CONSTRAINT uq_users_student_id UNIQUE (student_id),
    ADD CONSTRAINT uq_users_professor_id UNIQUE (professor_id),
    ADD CONSTRAINT uq_users_admin_id UNIQUE (admin_id),
    ADD CONSTRAINT uq_users_staff_id UNIQUE (staff_id);

ALTER TABLE enrollments
    ADD CONSTRAINT uq_enrollments_student_class UNIQUE (student_id, class_id);

ALTER TABLE grades
    ADD CONSTRAINT uq_grades_enrollment UNIQUE (enrollment_id);

ALTER TABLE classes
    ADD CONSTRAINT uq_classes_offering UNIQUE
    (subject_id, professor_id, section_id, semester_id, school_year_id);

-- =========================================================
-- 4) ADD CHECK CONSTRAINTS
-- =========================================================

ALTER TABLE students
    ADD CONSTRAINT chk_students_current_year_level
    CHECK (current_year_level BETWEEN 1 AND 6);

ALTER TABLE sections
    ADD CONSTRAINT chk_sections_section_year_level
    CHECK (section_year_level BETWEEN 1 AND 6);

ALTER TABLE classes
    ADD CONSTRAINT chk_classes_capacity
    CHECK (capacity IS NULL OR capacity > 0);

ALTER TABLE grades
    ADD CONSTRAINT chk_grades_prelim
    CHECK (prelim IS NULL OR (prelim >= 0 AND prelim <= 100)),
    ADD CONSTRAINT chk_grades_midterm
    CHECK (midterm IS NULL OR (midterm >= 0 AND midterm <= 100)),
    ADD CONSTRAINT chk_grades_finals
    CHECK (finals IS NULL OR (finals >= 0 AND finals <= 100)),
    ADD CONSTRAINT chk_grades_final_grade
    CHECK (final_grade IS NULL OR (final_grade >= 0 AND final_grade <= 100));

ALTER TABLE users
    ADD CONSTRAINT chk_users_exactly_one_profile
    CHECK (
        (CASE WHEN student_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN professor_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN admin_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN staff_id IS NOT NULL THEN 1 ELSE 0 END) = 1
    ),
    ADD CONSTRAINT chk_users_role_match
    CHECK (
        (role = 'student'   AND student_id IS NOT NULL AND professor_id IS NULL AND admin_id IS NULL AND staff_id IS NULL) OR
        (role = 'professor' AND professor_id IS NOT NULL AND student_id IS NULL AND admin_id IS NULL AND staff_id IS NULL) OR
        (role = 'admin'     AND admin_id IS NOT NULL AND student_id IS NULL AND professor_id IS NULL AND staff_id IS NULL) OR
        (role = 'staff'     AND staff_id IS NOT NULL AND student_id IS NULL AND professor_id IS NULL AND admin_id IS NULL)
    );

-- =========================================================
-- 5) NORMALIZE CLASS SCHEDULE
-- =========================================================

CREATE TABLE IF NOT EXISTS class_schedules (
    class_schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    day_of_week ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_class_schedules_class
        FOREIGN KEY (class_id) REFERENCES classes(class_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT chk_class_schedules_time
        CHECK (start_time < end_time),
    CONSTRAINT uq_class_schedules_slot
        UNIQUE (class_id, day_of_week, start_time, end_time)
);

-- =========================================================
-- 6) ADD PERFORMANCE INDEXES
-- =========================================================

CREATE INDEX idx_students_course_id ON students(course_id);
CREATE INDEX idx_professors_department_id ON professors(department_id);
CREATE INDEX idx_staffs_department_id ON staffs(department_id);
CREATE INDEX idx_subjects_department_id ON subjects(department_id);
CREATE INDEX idx_courses_department_id ON courses(department_id);
CREATE INDEX idx_sections_course_id ON sections(course_id);

CREATE INDEX idx_classes_subject_id ON classes(subject_id);
CREATE INDEX idx_classes_professor_id ON classes(professor_id);
CREATE INDEX idx_classes_section_id ON classes(section_id);
CREATE INDEX idx_classes_semester_id ON classes(semester_id);
CREATE INDEX idx_classes_school_year_id ON classes(school_year_id);

CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_class_id ON enrollments(class_id);
CREATE INDEX idx_grades_enrollment_id ON grades(enrollment_id);

-- =========================================================
-- 7) DEFAULTS
-- =========================================================

ALTER TABLE enrollments
    MODIFY COLUMN enrollment_date DATE NOT NULL DEFAULT (CURRENT_DATE);

ALTER TABLE enrollments
    MODIFY COLUMN status ENUM('enrolled','dropped','completed') NOT NULL DEFAULT 'enrolled';

COMMIT;
