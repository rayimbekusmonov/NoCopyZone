package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.dto.ProctoringDto;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.ProctoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProctoringController {

    private final ProctoringService proctoringService;

    // WebSocket orqali keluvchi event (talabadan)
    @MessageMapping("/event")
    public void handleProctoringEvent(@Payload ProctoringDto.ProctoringEvent event,
                                      @AuthenticationPrincipal User student) {
        if (student != null) {
            proctoringService.processEvent(event, student.getId());
        }
    }

    // REST orqali ham event yuborish (fallback)
    @PostMapping("/event")
    public ResponseEntity<Void> logEvent(@RequestBody ProctoringDto.ProctoringEvent event,
                                         @AuthenticationPrincipal User student) {
        proctoringService.processEvent(event, student.getId());
        return ResponseEntity.ok().build();
    }

    // Bitta submission hisoboti (ustoz uchun)
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<ProctoringDto.SubmissionReport> getSubmissionReport(
            @PathVariable Long submissionId) {
        return ResponseEntity.ok(proctoringService.getSubmissionReport(submissionId));
    }

    // Bitta task dagi barcha submission hisobotlari (ustoz uchun)
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ProctoringDto.SubmissionReport>> getTaskReports(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(proctoringService.getTaskReports(taskId));
    }
}