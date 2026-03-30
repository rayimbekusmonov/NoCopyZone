package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.dto.ProctoringDto;
import com.rayimbek.nocopyzone.entity.ProctoringLog;
import com.rayimbek.nocopyzone.entity.Submission;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.ProctoringLogRepository;
import com.rayimbek.nocopyzone.repository.SubmissionRepository;
import com.rayimbek.nocopyzone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProctoringService {

    private final ProctoringLogRepository proctoringLogRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Har bir shubhali harakat uchun ball ayirish
    private static final int HIGH_PENALTY   = 15;
    private static final int MEDIUM_PENALTY = 7;
    private static final int LOW_PENALTY    = 2;

    @Transactional
    public void processEvent(ProctoringDto.ProctoringEvent event, Long studentId) {
        Submission submission = submissionRepository.findById(event.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found: " + event.getSubmissionId()));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found: " + studentId));

        // Severity aniqlash
        String severity = determineSeverity(event.getEventType());

        // Log yozish
        ProctoringLog proctoringLog = new ProctoringLog();
        proctoringLog.setSubmission(submission);
        proctoringLog.setStudent(student);
        proctoringLog.setEventType(event.getEventType());
        proctoringLog.setSeverity(severity);
        proctoringLog.setDetails(event.getDetails());
        proctoringLog.setOccurredAt(LocalDateTime.now());
        proctoringLogRepository.save(proctoringLog);

        // Integrity score yangilash
        updateIntegrityScore(submission, severity);

        // Ustozga real-time alert yuborish
        ProctoringDto.RealTimeAlert alert = new ProctoringDto.RealTimeAlert(
                submission.getId(),
                student.getId(),
                student.getFullName(),
                event.getEventType(),
                severity,
                event.getDetails(),
                LocalDateTime.now()
        );

        // Ustoz o'z kurs submissionlarini kuzatadi
        Long courseId = submission.getTask().getCourse().getId();
        messagingTemplate.convertAndSend("/topic/proctor/" + courseId, alert);

        log.info("Proctoring event: student={}, type={}, severity={}, submission={}",
                student.getEmail(), event.getEventType(), severity, submission.getId());
    }

    private String determineSeverity(String eventType) {
        return switch (eventType) {
            case "TAB_SWITCH", "FOCUS_LOST", "COPY_PASTE" -> "HIGH";
            case "SUSPICIOUS_TYPING", "RIGHT_CLICK"       -> "MEDIUM";
            default                                        -> "LOW";
        };
    }

    private void updateIntegrityScore(Submission submission, String severity) {
        int penalty = switch (severity) {
            case "HIGH"   -> HIGH_PENALTY;
            case "MEDIUM" -> MEDIUM_PENALTY;
            default       -> LOW_PENALTY;
        };

        int newScore = Math.max(0, submission.getIntegrityScore() - penalty);
        submission.setIntegrityScore(newScore);
        submissionRepository.save(submission);
    }

    public ProctoringDto.SubmissionReport getSubmissionReport(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        List<ProctoringLog> logs = proctoringLogRepository
                .findBySubmissionIdOrderByOccurredAtDesc(submissionId);

        long highCount   = proctoringLogRepository.countHighSeverityBySubmission(submissionId);
        long mediumCount = proctoringLogRepository.countMediumSeverityBySubmission(submissionId);

        List<ProctoringDto.LogResponse> logResponses = logs.stream()
                .map(l -> new ProctoringDto.LogResponse(
                        l.getId(), l.getEventType(), l.getSeverity(),
                        l.getDetails(), l.getOccurredAt()))
                .collect(Collectors.toList());

        return new ProctoringDto.SubmissionReport(
                submissionId,
                submission.getStudent().getFullName(),
                submission.getStudent().getEmail(),
                submission.getIntegrityScore(),
                submission.getStatus(),
                highCount,
                mediumCount,
                logResponses
        );
    }

    public List<ProctoringDto.SubmissionReport> getTaskReports(Long taskId) {
        List<Submission> submissions = submissionRepository.findByTaskId(taskId);
        return submissions.stream()
                .map(s -> getSubmissionReport(s.getId()))
                .collect(Collectors.toList());
    }
}