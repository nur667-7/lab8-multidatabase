package com.university.dto;

import jakarta.validation.constraints.*;

public record CourseRequest(
        @NotBlank(message = "Course name is required")
        @Size(max = 150, message = "Course name cannot exceed 150 characters")
        String courseName,

        @NotBlank(message = "Course code is required")
        @Size(max = 50, message = "Course code cannot exceed 50 characters")
        String courseCode,

        @NotNull(message = "Credits is required")
        @Min(value = 1, message = "Credits must be at least 1")
        @Max(value = 30, message = "Credits cannot exceed 30")
        Integer credits,

        @NotBlank(message = "Instructor is required")
        @Size(max = 100, message = "Instructor name cannot exceed 100 characters")
        String instructor
) {
}
