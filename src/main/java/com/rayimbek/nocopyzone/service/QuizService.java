package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.*;
import com.rayimbek.nocopyzone.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizQuestionRepository questionRepository;
    private final QuizAnswerRepository answerRepository;
    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;

    // ===== DTOs =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private Long id;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private Integer orderNum;
        // correctAnswer student uchun ko'rinmaydi!
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionWithAnswerDto {
        private Long id;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private Integer orderNum;
    }

    @Data
    @NoArgsConstructor
    public static class CreateQuestionRequest {
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private Integer orderNum = 0;
    }

    @Data
    @NoArgsConstructor
    public static class SubmitAnswerRequest {
        private Long questionId;
        private String selected; // A, B, C, D
    }

    @Data
    @AllArgsConstructor
    public static class QuizResultDto {
        private int totalQuestions;
        private int correctAnswers;
        private int score;
        private int maxScore;
        private double percentage;
        private List<AnswerResultDto> answers;
    }

    @Data
    @AllArgsConstructor
    public static class AnswerResultDto {
        private Long questionId;
        private String questionText;
        private String selected;
        private String correctAnswer;
        private boolean isCorrect;
    }

    // ===== TEACHER: Savol qo'shish =====
    @Transactional
    public QuestionWithAnswerDto addQuestion(Long taskId, CreateQuestionRequest req) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task topilmadi: " + taskId));

        QuizQuestion q = new QuizQuestion();
        q.setTask(task);
        q.setQuestionText(req.getQuestionText());
        q.setOptionA(req.getOptionA());
        q.setOptionB(req.getOptionB());
        q.setOptionC(req.getOptionC());
        q.setOptionD(req.getOptionD());
        q.setCorrectAnswer(req.getCorrectAnswer().toUpperCase());
        q.setOrderNum(req.getOrderNum());
        q = questionRepository.save(q);
        return toWithAnswer(q);
    }

    // ===== TEACHER: Savollarni ko'rish =====
    public List<QuestionWithAnswerDto> getQuestionsWithAnswers(Long taskId) {
        return questionRepository.findByTaskIdOrderByOrderNum(taskId)
                .stream().map(this::toWithAnswer).collect(Collectors.toList());
    }

    // ===== STUDENT: Savollarni ko'rish (to'g'ri javob ko'rinmaydi) =====
    public List<QuestionDto> getQuestionsForStudent(Long taskId) {
        List<QuizQuestion> questions = questionRepository.findByTaskIdOrderByOrderNum(taskId);
        Collections.shuffle(questions); // Tasodifiy tartib
        return questions.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ===== STUDENT: Javob yuborish =====
    @Transactional
    public void submitAnswer(Long submissionId, SubmitAnswerRequest req) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission topilmadi"));

        if ("SUBMITTED".equals(submission.getStatus()) || "GRADED".equals(submission.getStatus())) {
            throw new RuntimeException("Topshiriq allaqachon topshirilgan");
        }

        QuizQuestion question = questionRepository.findById(req.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Savol topilmadi"));

        // Agar javob allaqachon berilgan bo'lsa — yangilash
        QuizAnswer answer;
        if (answerRepository.existsBySubmissionIdAndQuestionId(submissionId, req.getQuestionId())) {
            answer = answerRepository.findBySubmissionId(submissionId).stream()
                    .filter(a -> a.getQuestion().getId().equals(req.getQuestionId()))
                    .findFirst().orElseThrow();
        } else {
            answer = new QuizAnswer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
        }

        answer.setSelected(req.getSelected().toUpperCase());
        answer.setIsCorrect(req.getSelected().toUpperCase().equals(question.getCorrectAnswer()));
        answer.setAnsweredAt(LocalDateTime.now());
        answerRepository.save(answer);
    }

    // ===== STUDENT: Quiz yakunlash va ball hisoblash =====
    @Transactional
    public QuizResultDto finishQuiz(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission topilmadi"));

        int totalQ = (int) questionRepository.countByTaskId(submission.getTask().getId());
        long correct = answerRepository.countCorrectBySubmission(submissionId);

        int maxScore = submission.getTask().getMaxScore();
        int score = totalQ > 0 ? (int) Math.round((double) correct / totalQ * maxScore) : 0;

        submission.setScore(score);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        submissionRepository.save(submission);

        // Natijalar
        List<QuizAnswer> answers = answerRepository.findBySubmissionId(submissionId);
        List<AnswerResultDto> results = answers.stream()
                .map(a -> new AnswerResultDto(
                        a.getQuestion().getId(),
                        a.getQuestion().getQuestionText(),
                        a.getSelected(),
                        a.getQuestion().getCorrectAnswer(),
                        a.getIsCorrect()
                )).collect(Collectors.toList());

        double percentage = totalQ > 0 ? (double) correct / totalQ * 100 : 0;
        return new QuizResultDto(totalQ, (int) correct, score, maxScore, percentage, results);
    }

    // ===== TEACHER: Savol o'chirish =====
    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    private QuestionDto toDto(QuizQuestion q) {
        return new QuestionDto(q.getId(), q.getQuestionText(),
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getOrderNum());
    }

    private QuestionWithAnswerDto toWithAnswer(QuizQuestion q) {
        return new QuestionWithAnswerDto(q.getId(), q.getQuestionText(),
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                q.getCorrectAnswer(), q.getOrderNum());
    }
}