package com.university.course.service;

import com.university.course.entity.Course;
import com.university.course.repository.CourseRepository;
import com.university.dto.CourseRequest;
import com.university.dto.CourseResponse;
import com.university.exception.DuplicateResourceException;
import com.university.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true, transactionManager = "courseTransactionManager")
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .toList();
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));
        return CourseResponse.fromEntity(course);
    }

    public Course findCourseEntityById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));
    }

    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    @Transactional(transactionManager = "courseTransactionManager")
    public CourseResponse createCourse(CourseRequest request) {
        String normalizedCode = request.courseCode().trim().toUpperCase();
        if (courseRepository.existsByCourseCode(normalizedCode)) {
            throw new DuplicateResourceException("Course with code '" + normalizedCode + "' already exists");
        }

        Course course = new Course(
                request.courseName().trim(),
                normalizedCode,
                request.credits(),
                request.instructor().trim()
        );

        Course saved = courseRepository.save(course);
        return CourseResponse.fromEntity(saved);
    }

    @Transactional(transactionManager = "courseTransactionManager")
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + id + " not found"));

        String normalizedCode = request.courseCode().trim().toUpperCase();
        if (courseRepository.existsByCourseCodeAndIdNot(normalizedCode, id)) {
            throw new DuplicateResourceException("Course code '" + normalizedCode + "' is already in use");
        }

        course.setCourseName(request.courseName().trim());
        course.setCourseCode(normalizedCode);
        course.setCredits(request.credits());
        course.setInstructor(request.instructor().trim());

        Course updated = courseRepository.save(course);
        return CourseResponse.fromEntity(updated);
    }

    @Transactional(transactionManager = "courseTransactionManager")
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course with ID " + id + " not found");
        }
        courseRepository.deleteById(id);
    }
}
