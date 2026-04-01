package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/start/{taskId}")
    public ResponseEntity<SubmissionService.SubmissionResponse> start(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.startSubmission(taskId, student));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmissionService.SubmissionResponse> submit(
            @PathVariable Long id,
            @RequestBody(required = false) String content,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.submit(id, content != null ? content : "", student));
    }

    @PostMapping("/{id}/grade")
    public ResponseEntity<SubmissionService.SubmissionResponse> grade(
            @PathVariable Long id,
            @RequestBody SubmissionService.GradeRequest request) {
        return ResponseEntity.ok(submissionService.grade(id, request));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubmissionService.SubmissionResponse>> getByTask(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(submissionService.getByTask(taskId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<SubmissionService.SubmissionResponse>> getMySubmissions(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.getMySubmissions(student));
    }
}