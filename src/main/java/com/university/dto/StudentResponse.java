package com.university.dto;

import com.university.student.entity.Student;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String major,
        Integer enrollmentYear
) {
    public static StudentResponse fromEntity(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getMajor(),
                student.getEnrollmentYear()
        );
    }
}
