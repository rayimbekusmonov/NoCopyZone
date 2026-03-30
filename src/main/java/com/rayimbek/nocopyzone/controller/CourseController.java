package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.dto.CourseDto;
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
    public ResponseEntity<CourseDto.Response> create(
            @Valid @RequestBody CourseDto.CreateRequest request,
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(courseService.create(request, teacher));
    }

    @GetMapping
    public ResponseEntity<List<CourseDto.Response>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseDto.Response>> getMyCourses(
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(courseService.getMyCoures(teacher));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }
}