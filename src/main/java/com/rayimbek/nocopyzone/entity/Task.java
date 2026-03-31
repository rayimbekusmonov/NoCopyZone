package com.rayimbek.nocopyzone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // LAB, EXAM, CODE, QUIZ, ESSAY, FILE
    @Column(nullable = false, length = 50)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    private LocalDateTime deadline;

    // QUIZ uchun vaqt chegarasi (daqiqada)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Ustoz qo'ygan fayl (PDF topshiriq)
    @Column(name = "task_file_url", length = 500)
    private String taskFileUrl;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 100;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}