package com.university.dto;

import java.time.Instant;

public record CourseGradeDetailDto(
        Long courseId,
        String courseName,
        String courseCode,
        Integer credits,
        String instructor,
        Double score,
        String letterGrade,
        Instant gradedAt
) {
}
