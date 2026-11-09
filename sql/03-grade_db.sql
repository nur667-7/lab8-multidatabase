-- Connect to grade_db
-- \c grade_db;

DROP TABLE IF EXISTS grades CASCADE;

CREATE TABLE grades (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT NOT NULL, -- Logical reference to student_db.students(id)
    course_id BIGINT NOT NULL,  -- Logical reference to course_db.courses(id)
    score DOUBLE PRECISION NOT NULL CHECK (score >= 0 AND score <= 100),
    letter_grade VARCHAR(5) NOT NULL,
    graded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexing logical references for fast querying
CREATE INDEX idx_grades_student_id ON grades(student_id);
CREATE INDEX idx_grades_course_id ON grades(course_id);

-- Sample Initial Data for Testing (Linking student 1 to courses 1, 2, 3)
INSERT INTO grades (student_id, course_id, score, letter_grade, graded_at) VALUES
(1, 1, 95.0, 'A', CURRENT_TIMESTAMP),
(1, 2, 88.5, 'B+', CURRENT_TIMESTAMP),
(1, 3, 91.0, 'A-', CURRENT_TIMESTAMP),
(2, 1, 84.0, 'B', CURRENT_TIMESTAMP);
