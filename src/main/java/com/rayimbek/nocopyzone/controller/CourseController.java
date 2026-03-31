package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseService.Response> create(
            @RequestBody CourseService.CreateRequest request,
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(courseService.create(request, teacher));
    }

    @GetMapping
    public ResponseEntity<List<CourseService.Response>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseService.Response>> getMyCourses(
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(courseService.getMyCourses(teacher));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseService.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }
}