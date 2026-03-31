package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // Kursga yozilish
    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<EnrollmentService.EnrollmentResponse> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(enrollmentService.enroll(courseId, student));
    }

    // Kursdan chiqish
    @DeleteMapping("/unenroll/{courseId}")
    public ResponseEntity<Void> unenroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        enrollmentService.unenroll(courseId, student);
        return ResponseEntity.ok().build();
    }

    // Mening kurslarim (student)
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentService.EnrollmentResponse>> myEnrollments(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(student));
    }

    // Kurs talabalari (ustoz)
    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<List<EnrollmentService.EnrollmentResponse>> courseStudents(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseStudents(courseId));
    }
}