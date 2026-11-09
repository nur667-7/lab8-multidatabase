package com.university.dto;

import jakarta.validation.constraints.*;

public record GradeRequest(
        @NotNull(message = "Student ID is required")
        @Positive(message = "Student ID must be a positive number")
        Long studentId,

        @NotNull(message = "Course ID is required")
        @Positive(message = "Course ID must be a positive number")
        Long courseId,

        @NotNull(message = "Score is required")
        @DecimalMin(value = "0.0", message = "Score must be at least 0.0")
        @DecimalMax(value = "100.0", message = "Score cannot exceed 100.0")
        Double score,

        @Size(max = 5, message = "Letter grade cannot exceed 5 characters")
        String letterGrade
) {
}
