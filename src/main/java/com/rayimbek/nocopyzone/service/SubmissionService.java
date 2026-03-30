package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Submission;
import com.rayimbek.nocopyzone.entity.Task;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.SubmissionRepository;
import com.rayimbek.nocopyzone.repository.TaskRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;

    @Data
    public static class SubmissionResponse {
        private Long id;
        private Long taskId;
        private String taskTitle;
        private String status;
        private Integer integrityScore;
        private String content;
        private LocalDateTime startedAt;
        private LocalDateTime submittedAt;
    }

    @Transactional
    public SubmissionResponse startSubmission(Long taskId, User student) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        // Agar avval boshlangan bo'lsa — yangisini yaratma
        return submissionRepository.findByTaskIdAndStudentId(taskId, student.getId())
                .map(this::toResponse)
                .orElseGet(() -> {
                    Submission submission = new Submission();
                    submission.setTask(task);
                    submission.setStudent(student);
                    submission.setStatus("IN_PROGRESS");
                    submission.setIntegrityScore(100);
                    submission.setStartedAt(LocalDateTime.now());
                    return toResponse(submissionRepository.save(submission));
                });
    }

    @Transactional
    public SubmissionResponse submitWork(Long submissionId, String content, User student) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Access denied");
        }

        submission.setContent(content);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        return toResponse(submissionRepository.save(submission));
    }

    private SubmissionResponse toResponse(Submission s) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(s.getId());
        response.setTaskId(s.getTask().getId());
        response.setTaskTitle(s.getTask().getTitle());
        response.setStatus(s.getStatus());
        response.setIntegrityScore(s.getIntegrityScore());
        response.setContent(s.getContent());
        response.setStartedAt(s.getStartedAt());
        response.setSubmittedAt(s.getSubmittedAt());
        return response;
    }
}