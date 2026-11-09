package com.university.student.service;

import com.university.dto.StudentRequest;
import com.university.dto.StudentResponse;
import com.university.exception.DuplicateResourceException;
import com.university.exception.ResourceNotFoundException;
import com.university.student.entity.Student;
import com.university.student.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true, transactionManager = "studentTransactionManager")
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentResponse::fromEntity)
                .toList();
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + id + " not found"));
        return StudentResponse.fromEntity(student);
    }

    public Student findStudentEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + id + " not found"));
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }

    @Transactional(transactionManager = "studentTransactionManager")
    public StudentResponse createStudent(StudentRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (studentRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Student with email '" + normalizedEmail + "' already exists");
        }

        Student student = new Student(
                request.firstName().trim(),
                request.lastName().trim(),
                normalizedEmail,
                request.major().trim(),
                request.enrollmentYear()
        );

        Student saved = studentRepository.save(student);
        return StudentResponse.fromEntity(saved);
    }

    @Transactional(transactionManager = "studentTransactionManager")
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + id + " not found"));

        String normalizedEmail = request.email().trim().toLowerCase();
        if (studentRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new DuplicateResourceException("Email '" + normalizedEmail + "' is already in use by another student");
        }

        student.setFirstName(request.firstName().trim());
        student.setLastName(request.lastName().trim());
        student.setEmail(normalizedEmail);
        student.setMajor(request.major().trim());
        student.setEnrollmentYear(request.enrollmentYear());

        Student updated = studentRepository.save(student);
        return StudentResponse.fromEntity(updated);
    }

    @Transactional(transactionManager = "studentTransactionManager")
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student with ID " + id + " not found");
        }
        studentRepository.deleteById(id);
    }
}
