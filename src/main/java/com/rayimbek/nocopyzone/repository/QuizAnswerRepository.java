package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findBySubmissionId(Long submissionId);

    @Query("SELECT COUNT(a) FROM QuizAnswer a WHERE a.submission.id = :subId AND a.isCorrect = true")
    long countCorrectBySubmission(@Param("subId") Long submissionId);

    boolean existsBySubmissionIdAndQuestionId(Long submissionId, Long questionId);
}