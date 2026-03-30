package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionService submissionService;

    // Topshiriqni boshlash
    @PostMapping("/start/{taskId}")
    public ResponseEntity<SubmissionService.SubmissionResponse> start(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.startSubmission(taskId, student));
    }

    // Topshiriqni yuborish
    @PostMapping("/{submissionId}/submit")
    public ResponseEntity<SubmissionService.SubmissionResponse> submit(
            @PathVariable Long submissionId,
            @RequestBody String content,
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(submissionService.submitWork(submissionId, content, student));
    }
}