package com.rayimbek.nocopyzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProctoringDto {

    // Frontend dan keluvchi event
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProctoringEvent {
        private Long submissionId;
        private String eventType;   // TAB_SWITCH, COPY_PASTE, FOCUS_LOST, SUSPICIOUS_TYPING, SCREENSHOT
        private String details;
    }

    // Log response
    @Data
    @AllArgsConstructor
    public static class LogResponse {
        private Long id;
        private String eventType;
        private String severity;
        private String details;
        private LocalDateTime occurredAt;
    }

    // Ustoz uchun submission hisoboti
    @Data
    @AllArgsConstructor
    public static class SubmissionReport {
        private Long submissionId;
        private String studentName;
        private String studentEmail;
        private Integer integrityScore;
        private String status;
        private long highRiskEvents;
        private long mediumRiskEvents;
        private List<LogResponse> logs;
    }

    // Real-time alert (WebSocket orqali ustozga)
    @Data
    @AllArgsConstructor
    public static class RealTimeAlert {
        private Long submissionId;
        private Long studentId;
        private String studentName;
        private String eventType;
        private String severity;
        private String details;
        private LocalDateTime occurredAt;
    }
}