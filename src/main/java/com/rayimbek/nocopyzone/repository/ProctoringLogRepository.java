package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.ProctoringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProctoringLogRepository extends JpaRepository<ProctoringLog, Long> {

    List<ProctoringLog> findBySubmissionIdOrderByOccurredAtDesc(Long submissionId);

    List<ProctoringLog> findByStudentIdOrderByOccurredAtDesc(Long studentId);

    @Query("SELECT COUNT(p) FROM ProctoringLog p WHERE p.submission.id = :submissionId AND p.severity = 'HIGH'")
    long countHighSeverityBySubmission(@Param("submissionId") Long submissionId);

    @Query("SELECT COUNT(p) FROM ProctoringLog p WHERE p.submission.id = :submissionId AND p.severity = 'MEDIUM'")
    long countMediumSeverityBySubmission(@Param("submissionId") Long submissionId);
}