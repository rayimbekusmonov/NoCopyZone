package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LectureController {

    private final LectureService lectureService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<LectureService.LectureResponse> create(
            @PathVariable Long courseId,
            @RequestBody LectureService.CreateRequest request) {
        return ResponseEntity.ok(lectureService.create(courseId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectureService.LectureResponse> update(
            @PathVariable Long id,
            @RequestBody LectureService.CreateRequest request) {
        return ResponseEntity.ok(lectureService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lectureService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LectureService.LectureResponse>> getByCourse(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(lectureService.getByCourse(courseId));
    }
}