-- Connect to student_db
-- \c student_db;

DROP TABLE IF EXISTS students CASCADE;

CREATE TABLE students (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    major VARCHAR(100) NOT NULL,
    enrollment_year INTEGER NOT NULL
);

-- Sample Initial Data for Testing
INSERT INTO students (first_name, last_name, email, major, enrollment_year) VALUES
('Alan', 'Turing', 'alan.turing@university.edu', 'Computer Science', 2023),
('Ada', 'Lovelace', 'ada.lovelace@university.edu', 'Mathematics', 2022),
('Grace', 'Hopper', 'grace.hopper@university.edu', 'Software Engineering', 2024);
