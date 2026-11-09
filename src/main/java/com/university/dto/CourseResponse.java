package com.university.dto;

import com.university.course.entity.Course;

public record CourseResponse(
        Long id,
        String courseName,
        String courseCode,
        Integer credits,
        String instructor
) {
    public static CourseResponse fromEntity(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCredits(),
                course.getInstructor()
        );
    }
}
