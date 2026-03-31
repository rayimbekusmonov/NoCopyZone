package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByTaskIdOrderByOrderNum(Long taskId);
    long countByTaskId(Long taskId);
    void deleteByTaskId(Long taskId);
}