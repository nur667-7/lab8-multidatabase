package com.university.grade.service;

import com.university.course.entity.Course;
import com.university.course.repository.CourseRepository;
import com.university.dto.*;
import com.university.exception.InvalidDataException;
import com.university.exception.ResourceNotFoundException;
import com.university.grade.entity.Grade;
import com.university.grade.repository.GradeRepository;
import com.university.student.entity.Student;
import com.university.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public GradeService(
            GradeRepository gradeRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(transactionManager = "gradeTransactionManager")
    public GradeResponse assignGrade(GradeRequest request) {
        // 1. Service layer existence check for student (cross-database validation)
        if (!studentRepository.existsById(request.studentId())) {
            throw new ResourceNotFoundException("Cannot assign grade: Student with ID " + request.studentId() + " does not exist in student_db");
        }

        // 2. Service layer existence check for course (cross-database validation)
        if (!courseRepository.existsById(request.courseId())) {
            throw new ResourceNotFoundException("Cannot assign grade: Course with ID " + request.courseId() + " does not exist in course_db");
        }

        // 3. Score bounds validation
        if (request.score() < 0.0 || request.score() > 100.0) {
            throw new InvalidDataException("Score must be between 0.0 and 100.0. Provided: " + request.score());
        }

        // 4. Resolve letter grade
        String letterGrade = (request.letterGrade() != null && !request.letterGrade().isBlank())
                ? request.letterGrade().trim().toUpperCase()
                : computeLetterGrade(request.score());

        Grade grade = new Grade(
                request.studentId(),
                request.courseId(),
                request.score(),
                letterGrade,
                Instant.now()
        );

        Grade saved = gradeRepository.save(grade);
        return GradeResponse.fromEntity(saved);
    }

    public List<GradeResponse> getGradesByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with ID " + studentId + " not found in student_db");
        }
        return gradeRepository.findByStudentId(studentId).stream()
                .map(GradeResponse::fromEntity)
                .toList();
    }

    /**
     * Cross-Database Data Aggregation:
     * Combines data from student_db, course_db, and grade_db into a single StudentTranscriptDto.
     */
    public StudentTranscriptDto getStudentTranscript(Long studentId) {
        // 1. Fetch Student from student_db
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found in student_db"));

        // 2. Fetch all Grades for this student from grade_db
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        List<CourseGradeDetailDto> courseDetails = new ArrayList<>();
        double totalScore = 0.0;
        int totalCredits = 0;

        // 3. For each grade, resolve Course from course_db
        for (Grade grade : grades) {
            Course course = courseRepository.findById(grade.getCourseId())
                    .orElse(null);

            String courseName = course != null ? course.getCourseName() : "Unknown Course [ID: " + grade.getCourseId() + "]";
            String courseCode = course != null ? course.getCourseCode() : "N/A";
            Integer credits = course != null ? course.getCredits() : 0;
            String instructor = course != null ? course.getInstructor() : "N/A";

            courseDetails.add(new CourseGradeDetailDto(
                    grade.getCourseId(),
                    courseName,
                    courseCode,
                    credits,
                    instructor,
                    grade.getScore(),
                    grade.getLetterGrade(),
                    grade.getGradedAt()
            ));

            totalScore += grade.getScore();
            totalCredits += credits;
        }

        Double averageScore = 0.0;
        if (!grades.isEmpty()) {
            double rawAverage = totalScore / grades.size();
            averageScore = BigDecimal.valueOf(rawAverage)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        String fullName = student.getFirstName() + " " + student.getLastName();

        return new StudentTranscriptDto(
                student.getId(),
                fullName,
                student.getEmail(),
                student.getMajor(),
                student.getEnrollmentYear(),
                courseDetails,
                averageScore,
                totalCredits
        );
    }

    public static String computeLetterGrade(double score) {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }
}
