package com.university;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.dto.CourseRequest;
import com.university.dto.GradeRequest;
import com.university.dto.StudentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(OrderAnnotation.class)
class UniversityManagementSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("1. Create Student - Success (201 Created)")
    void testCreateStudentSuccess() throws Exception {
        StudentRequest request = new StudentRequest(
                "Alan",
                "Turing",
                "alan.turing@university.edu",
                "Computer Science",
                2023
        );

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is("Alan")))
                .andExpect(jsonPath("$.lastName", is("Turing")))
                .andExpect(jsonPath("$.email", is("alan.turing@university.edu")))
                .andExpect(jsonPath("$.major", is("Computer Science")))
                .andExpect(jsonPath("$.enrollmentYear", is(2023)));
    }

    @Test
    @Order(2)
    @DisplayName("2. Create Student - Duplicate Email Error (409 Conflict)")
    void testCreateStudentDuplicateEmail() throws Exception {
        StudentRequest duplicate = new StudentRequest(
                "Duplicate",
                "User",
                "alan.turing@university.edu",
                "Physics",
                2024
        );

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @Order(3)
    @DisplayName("3. List Students - (200 OK)")
    void testListStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].email", is("alan.turing@university.edu")));
    }

    @Test
    @Order(4)
    @DisplayName("4. Create Course - Success (201 Created)")
    void testCreateCourseSuccess() throws Exception {
        CourseRequest request = new CourseRequest(
                "Distributed Systems",
                "CS401",
                4,
                "Dr. Leslie Lamport"
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.courseName", is("Distributed Systems")))
                .andExpect(jsonPath("$.courseCode", is("CS401")))
                .andExpect(jsonPath("$.credits", is(4)))
                .andExpect(jsonPath("$.instructor", is("Dr. Leslie Lamport")));
    }

    @Test
    @Order(5)
    @DisplayName("5. Create Course - Duplicate Course Code (409 Conflict)")
    void testCreateCourseDuplicateCode() throws Exception {
        CourseRequest duplicate = new CourseRequest(
                "Another Distributed Course",
                "CS401",
                3,
                "Prof. Anon"
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @Order(6)
    @DisplayName("6. Create Second Course for Aggregation (201 Created)")
    void testCreateSecondCourse() throws Exception {
        CourseRequest course2 = new CourseRequest(
                "Database Systems",
                "CS301",
                3,
                "Dr. Edgar Codd"
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseCode", is("CS301")));
    }

    @Test
    @Order(7)
    @DisplayName("7. Assign Grade - Invalid Score Bounds (400 Bad Request)")
    void testAssignGradeInvalidScore() throws Exception {
        GradeRequest request = new GradeRequest(1L, 1L, 150.0, null);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(8)
    @DisplayName("8. Assign Grade - Non-existent Student (404 Not Found)")
    void testAssignGradeNonExistentStudent() throws Exception {
        GradeRequest request = new GradeRequest(99999L, 1L, 85.0, "B");

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Student with ID 99999 does not exist")));
    }

    @Test
    @Order(9)
    @DisplayName("9. Assign Grade - Non-existent Course (404 Not Found)")
    void testAssignGradeNonExistentCourse() throws Exception {
        GradeRequest request = new GradeRequest(1L, 99999L, 85.0, "B");

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Course with ID 99999 does not exist")));
    }

    @Test
    @Order(10)
    @DisplayName("10. Assign Grades - Valid Cross-Database References (201 Created)")
    void testAssignGradesValid() throws Exception {
        // Grade 1: Course 1 -> 95.0 (A)
        GradeRequest grade1 = new GradeRequest(1L, 1L, 95.0, null);
        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grade1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.courseId", is(1)))
                .andExpect(jsonPath("$.score", is(95.0)))
                .andExpect(jsonPath("$.letterGrade", is("A")));

        // Grade 2: Course 2 -> 85.0 (B)
        GradeRequest grade2 = new GradeRequest(1L, 2L, 85.0, "B");
        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grade2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.letterGrade", is("B")));
    }

    @Test
    @Order(11)
    @DisplayName("11. Get Grades by Student ID (200 OK)")
    void testGetGradesByStudentId() throws Exception {
        mockMvc.perform(get("/api/grades/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentId", is(1)))
                .andExpect(jsonPath("$[1].studentId", is(1)));
    }

    @Test
    @Order(12)
    @DisplayName("12. Final Challenge: Cross-Database Student Transcript Aggregation (200 OK)")
    void testGetStudentTranscriptAggregation() throws Exception {
        mockMvc.perform(get("/api/students/1/transcript"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.studentName", is("Alan Turing")))
                .andExpect(jsonPath("$.email", is("alan.turing@university.edu")))
                .andExpect(jsonPath("$.major", is("Computer Science")))
                .andExpect(jsonPath("$.courses", hasSize(2)))
                .andExpect(jsonPath("$.courses[0].courseCode", is("CS401")))
                .andExpect(jsonPath("$.courses[0].courseName", is("Distributed Systems")))
                .andExpect(jsonPath("$.courses[0].score", is(95.0)))
                .andExpect(jsonPath("$.courses[0].letterGrade", is("A")))
                .andExpect(jsonPath("$.courses[1].courseCode", is("CS301")))
                .andExpect(jsonPath("$.courses[1].courseName", is("Database Systems")))
                .andExpect(jsonPath("$.courses[1].score", is(85.0)))
                .andExpect(jsonPath("$.courses[1].letterGrade", is("B")))
                .andExpect(jsonPath("$.averageScore", is(90.0)))
                .andExpect(jsonPath("$.totalCredits", is(7))); // 4 credits + 3 credits
    }

    @Test
    @Order(13)
    @DisplayName("13. Student Transcript - Non-existent Student (404 Not Found)")
    void testGetStudentTranscriptNotFound() throws Exception {
        mockMvc.perform(get("/api/students/99999/transcript"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Student with ID 99999 not found")));
    }

    @Test
    @Order(14)
    @DisplayName("14. Case-Insensitive Duplicate Email & Course Code Checks (409 Conflict)")
    void testCaseInsensitiveDuplicates() throws Exception {
        StudentRequest dupStudent = new StudentRequest(
                "Alan",
                "Turing",
                "ALAN.TURING@UNIVERSITY.EDU",
                "Math",
                2024
        );
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupStudent)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        CourseRequest dupCourse = new CourseRequest(
                "Distributed Systems Copy",
                "cs401",
                3,
                "Dr. Smith"
        );
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupCourse)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(15)
    @DisplayName("15. Update Student & Course (200 OK)")
    void testUpdateStudentAndCourse() throws Exception {
        StudentRequest updateStudent = new StudentRequest(
                "Alan",
                "Turing",
                "alan.turing@university.edu",
                "Theoretical Computer Science",
                2023
        );
        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.major", is("Theoretical Computer Science")));

        CourseRequest updateCourse = new CourseRequest(
                "Advanced Distributed Systems",
                "CS401",
                5,
                "Dr. Leslie Lamport"
        );
        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credits", is(5)));
    }

    @Test
    @Order(16)
    @DisplayName("16. Delete Course & Student (204 No Content)")
    void testDeleteCourseAndStudent() throws Exception {
        mockMvc.perform(delete("/api/courses/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isNotFound());
    }
}
