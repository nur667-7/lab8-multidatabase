package com.university.dto;

import com.university.grade.entity.Grade;

import java.time.Instant;

public record GradeResponse(
        Long id,
        Long studentId,
        Long courseId,
        Double score,
        String letterGrade,
        Instant gradedAt
) {
    public static GradeResponse fromEntity(Grade grade) {
        return new GradeResponse(
                grade.getId(),
                grade.getStudentId(),
                grade.getCourseId(),
                grade.getScore(),
                grade.getLetterGrade(),
                grade.getGradedAt()
        );
    }
}
