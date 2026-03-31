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

    // Student o'zi kursga yoziladi
    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<EnrollmentService.EnrollmentResponse> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(enrollmentService.enroll(courseId, student));
    }

    // Student kursdan chiqadi
    @DeleteMapping("/unenroll/{courseId}")
    public ResponseEntity<Void> unenroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        enrollmentService.unenroll(courseId, student);
        return ResponseEntity.ok().build();
    }

    // Teacher o'z kursiga student qo'shadi
    @PostMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<EnrollmentService.EnrollmentResponse> enrollStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.enrollById(courseId, studentId));
    }

    // Teacher kursidan student chiqaradi
    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        enrollmentService.unenrollById(courseId, studentId);
        return ResponseEntity.ok().build();
    }

    // Student o'z kurslarini ko'radi
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentService.EnrollmentResponse>> myEnrollments(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(student));
    }

    // Kurs talabalari (teacher/admin uchun)
    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<List<EnrollmentService.EnrollmentResponse>> courseStudents(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseStudents(courseId));
    }
}