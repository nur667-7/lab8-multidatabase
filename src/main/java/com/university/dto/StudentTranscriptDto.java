package com.university.dto;

import java.util.List;

public record StudentTranscriptDto(
        Long studentId,
        String studentName,
        String email,
        String major,
        Integer enrollmentYear,
        List<CourseGradeDetailDto> courses,
        Double averageScore,
        Integer totalCredits
) {
}
