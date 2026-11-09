-- Connect to course_db
-- \c course_db;

DROP TABLE IF EXISTS courses CASCADE;

CREATE TABLE courses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_name VARCHAR(150) NOT NULL,
    course_code VARCHAR(50) NOT NULL UNIQUE,
    credits INTEGER NOT NULL,
    instructor VARCHAR(100) NOT NULL
);

-- Sample Initial Data for Testing
INSERT INTO courses (course_name, course_code, credits, instructor) VALUES
('Introduction to Computer Science', 'CS101', 4, 'Dr. Richard Feynman'),
('Data Structures and Algorithms', 'CS201', 4, 'Dr. Donald Knuth'),
('Database Management Systems', 'CS301', 3, 'Dr. Edgar Codd');
