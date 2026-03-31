package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    // ===== TEACHER =====

    // Savol qo'shish
    @PostMapping("/task/{taskId}/questions")
    public ResponseEntity<QuizService.QuestionWithAnswerDto> addQuestion(
            @PathVariable Long taskId,
            @RequestBody QuizService.CreateQuestionRequest request) {
        return ResponseEntity.ok(quizService.addQuestion(taskId, request));
    }

    // Barcha savollar (to'g'ri javob bilan - ustoz uchun)
    @GetMapping("/task/{taskId}/questions/admin")
    public ResponseEntity<List<QuizService.QuestionWithAnswerDto>> getQuestionsAdmin(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(quizService.getQuestionsWithAnswers(taskId));
    }

    // Savol o'chirish
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    // ===== STUDENT =====

    // Savollarni olish (to'g'ri javobsiz, tasodifiy tartibda)
    @GetMapping("/task/{taskId}/questions")
    public ResponseEntity<List<QuizService.QuestionDto>> getQuestionsForStudent(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(quizService.getQuestionsForStudent(taskId));
    }

    // Javob yuborish
    @PostMapping("/submission/{submissionId}/answer")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable Long submissionId,
            @RequestBody QuizService.SubmitAnswerRequest request) {
        quizService.submitAnswer(submissionId, request);
        return ResponseEntity.ok().build();
    }

    // Quiz yakunlash
    @PostMapping("/submission/{submissionId}/finish")
    public ResponseEntity<QuizService.QuizResultDto> finishQuiz(
            @PathVariable Long submissionId) {
        return ResponseEntity.ok(quizService.finishQuiz(submissionId));
    }
}