package com.rayimbek.nocopyzone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TaskDto {

    @Data
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank
        private String title;
        private String description;
        @NotBlank
        private String type; // LAB, PRESENTATION, EXAM, CODE
        @NotNull
        private Long courseId;
        private LocalDateTime deadline;
        private Integer maxScore = 100;
    }

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String type;
        private Long courseId;
        private String courseName;
        private String teacherName;
        private LocalDateTime deadline;
        private Integer maxScore;
        private LocalDateTime createdAt;
    }
}