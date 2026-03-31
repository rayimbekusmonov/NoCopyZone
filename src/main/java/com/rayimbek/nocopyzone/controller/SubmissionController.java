package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.SubmissionService;
import lombok.Data;
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

    @PostMapping("/{submissionId}/submit")
    public ResponseEntity<SubmissionService.SubmissionResponse> submit(
            @PathVariable Long submissionId,
            @RequestBody String content,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.submitWork(submissionId, content, student));
    }

    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<SubmissionService.SubmissionResponse> grade(
            @PathVariable Long submissionId,
            @RequestBody GradeRequest request) {
        return ResponseEntity.ok(submissionService.gradeSubmission(submissionId, request.getScore(), request.getFeedback()));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubmissionService.SubmissionResponse>> getByTask(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(submissionService.getByTask(taskId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<SubmissionService.SubmissionResponse>> getMy(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.getMySubmissions(student));
    }

    @Data
    public static class GradeRequest {
        private Integer score;
        private String feedback;
    }
}