package com.university.grade.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "letter_grade", nullable = false, length = 5)
    private String letterGrade;

    @Column(name = "graded_at", nullable = false)
    private Instant gradedAt;

    public Grade() {
    }

    public Grade(Long studentId, Long courseId, Double score, String letterGrade, Instant gradedAt) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
        this.letterGrade = letterGrade;
        this.gradedAt = gradedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.gradedAt == null) {
            this.gradedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(Instant gradedAt) {
        this.gradedAt = gradedAt;
    }
}
