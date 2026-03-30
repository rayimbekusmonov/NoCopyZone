package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByTaskIdAndStudentId(Long taskId, Long studentId);

    List<Submission> findByTaskId(Long taskId);

    List<Submission> findByStudentId(Long studentId);
}