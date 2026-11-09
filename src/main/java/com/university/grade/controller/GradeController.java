package com.university.grade.controller;

import com.university.dto.GradeRequest;
import com.university.dto.GradeResponse;
import com.university.grade.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    public ResponseEntity<GradeResponse> assignGrade(@Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.assignGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<List<GradeResponse>> getStudentGrades(@PathVariable("id") Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId));
    }
}
