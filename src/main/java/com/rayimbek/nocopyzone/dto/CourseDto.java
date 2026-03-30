package com.rayimbek.nocopyzone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CourseDto {

    @Data
    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank
        private String name;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String teacherName;
        private LocalDateTime createdAt;
    }
}