package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Submission;
import com.rayimbek.nocopyzone.entity.Task;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.SubmissionRepository;
import com.rayimbek.nocopyzone.repository.TaskRepository;
import lombok.AllArgsConstructor;
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
    @AllArgsConstructor
    public static class SubmissionResponse {
        private Long id;
        private Long taskId;
        private String taskTitle;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private String status;
        private String content;
        private String fileUrl;
        private Integer score;
        private Integer integrityScore;
        private String feedback;
        private LocalDateTime startedAt;
        private LocalDateTime submittedAt;
    }

    @Data
    public static class GradeRequest {
        private Integer score;
        private String feedback;
    }

    // Topshiriq boshlash yoki mavjudini qaytarish
    @Transactional
    public SubmissionResponse startSubmission(Long taskId, User student) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi: " + taskId));

        return submissionRepository.findByStudentIdAndTaskId(student.getId(), taskId)
                .map(this::toResponse)
                .orElseGet(() -> {
                    Submission s = new Submission();
                    s.setTask(task);
                    s.setStudent(student);
                    s.setStatus("IN_PROGRESS");
                    s.setIntegrityScore(100);
                    s.setContent("");
                    return toResponse(submissionRepository.save(s));
                });
    }

    // Matn topshirish
    @Transactional
    public SubmissionResponse submit(Long submissionId, String content, User student) {
        Submission s = getSubmission(submissionId, student);
        if ("GRADED".equals(s.getStatus())) throw new RuntimeException("Allaqachon baholangan");
        s.setContent(content);
        s.setStatus("SUBMITTED");
        s.setSubmittedAt(LocalDateTime.now());
        return toResponse(submissionRepository.save(s));
    }

    // Baholash
    @Transactional
    public SubmissionResponse grade(Long submissionId, GradeRequest req) {
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission topilmadi: " + submissionId));
        s.setScore(req.getScore());
        s.setFeedback(req.getFeedback());
        s.setStatus("GRADED");
        return toResponse(submissionRepository.save(s));
    }

    // Task bo'yicha barcha submissionlar (teacher uchun)
    public List<SubmissionResponse> getByTask(Long taskId) {
        return submissionRepository.findByTaskId(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Mening submissionlarim (student uchun)
    public List<SubmissionResponse> getMySubmissions(User student) {
        return submissionRepository.findByStudentId(student.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Submission getSubmission(Long id, User student) {
        return submissionRepository.findByIdAndStudentId(id, student.getId())
                .orElseThrow(() -> new RuntimeException("Submission topilmadi yoki ruxsat yo'q"));
    }

    private SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(
                s.getId(), s.getTask().getId(), s.getTask().getTitle(),
                s.getStudent().getId(), s.getStudent().getFullName(), s.getStudent().getEmail(),
                s.getStatus(), s.getContent(), s.getFileUrl(),
                s.getScore(), s.getIntegrityScore(), s.getFeedback(),
                s.getStartedAt(), s.getSubmittedAt());
    }
}