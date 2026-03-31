package com.rayimbek.nocopyzone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "order_num")
    private Integer orderNum = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}