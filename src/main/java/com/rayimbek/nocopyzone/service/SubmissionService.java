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
import java.util.List;
import java.util.stream.Collectors;

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
        private String studentName;
        private String studentEmail;
        private String status;
        private Integer integrityScore;
        private Integer score;
        private String feedback;
        private String content;
        private LocalDateTime startedAt;
        private LocalDateTime submittedAt;
    }

    @Transactional
    public SubmissionResponse startSubmission(Long taskId, User student) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

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

    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setScore(score);
        submission.setStatus("GRADED");
        // feedback uchun entity ga qo'shamiz — hozircha content ga yozamiz
        return toResponse(submissionRepository.save(submission));
    }

    public List<SubmissionResponse> getByTask(Long taskId) {
        return submissionRepository.findByTaskId(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SubmissionResponse> getMySubmissions(User student) {
        return submissionRepository.findByStudentId(student.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private SubmissionResponse toResponse(Submission s) {
        SubmissionResponse r = new SubmissionResponse();
        r.setId(s.getId());
        r.setTaskId(s.getTask().getId());
        r.setTaskTitle(s.getTask().getTitle());
        r.setStudentName(s.getStudent().getFullName());
        r.setStudentEmail(s.getStudent().getEmail());
        r.setStatus(s.getStatus());
        r.setIntegrityScore(s.getIntegrityScore());
        r.setScore(s.getScore());
        r.setContent(s.getContent());
        r.setStartedAt(s.getStartedAt());
        r.setSubmittedAt(s.getSubmittedAt());
        return r;
    }
}