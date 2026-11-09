package com.university.dto;

import jakarta.validation.constraints.*;

public record StudentRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        String email,

        @NotBlank(message = "Major is required")
        @Size(max = 100, message = "Major cannot exceed 100 characters")
        String major,

        @NotNull(message = "Enrollment year is required")
        @Min(value = 1900, message = "Enrollment year must be after 1900")
        @Max(value = 2100, message = "Enrollment year must be realistic")
        Integer enrollmentYear
) {
}
