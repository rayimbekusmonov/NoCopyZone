package com.rayimbek.nocopyzone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TaskDto {

    @Data
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private String type;
        private Long courseId;
        private LocalDateTime deadline;
        private Integer maxScore = 100;
        private Integer durationMinutes;
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
        private Integer durationMinutes;
        private LocalDateTime createdAt;
    }
}