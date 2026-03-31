package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.Lecture;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import com.rayimbek.nocopyzone.repository.LectureRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;

    @Data
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private String content;
        private String fileUrl;
        private String videoUrl;
        private Integer orderNum = 0;
    }

    @Data
    @AllArgsConstructor
    public static class LectureResponse {
        private Long id;
        private Long courseId;
        private String courseName;
        private String title;
        private String description;
        private String content;
        private String fileUrl;
        private String videoUrl;
        private Integer orderNum;
        private LocalDateTime createdAt;
    }

    @Transactional
    public LectureResponse create(Long courseId, CreateRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi: " + courseId));
        Lecture lecture = new Lecture();
        lecture.setCourse(course);
        lecture.setTitle(req.getTitle());
        lecture.setDescription(req.getDescription());
        lecture.setContent(req.getContent());
        lecture.setFileUrl(req.getFileUrl());
        lecture.setVideoUrl(req.getVideoUrl());
        lecture.setOrderNum(req.getOrderNum() != null ? req.getOrderNum() : 0);
        return toResponse(lectureRepository.save(lecture));
    }

    @Transactional
    public LectureResponse update(Long id, CreateRequest req) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ma'ruza topilmadi: " + id));
        lecture.setTitle(req.getTitle());
        lecture.setDescription(req.getDescription());
        lecture.setContent(req.getContent());
        lecture.setFileUrl(req.getFileUrl());
        lecture.setVideoUrl(req.getVideoUrl());
        if (req.getOrderNum() != null) lecture.setOrderNum(req.getOrderNum());
        return toResponse(lectureRepository.save(lecture));
    }

    @Transactional
    public void delete(Long id) {
        lectureRepository.deleteById(id);
    }

    public List<LectureResponse> getByCourse(Long courseId) {
        return lectureRepository.findByCourseIdOrderByOrderNum(courseId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private LectureResponse toResponse(Lecture l) {
        return new LectureResponse(
                l.getId(), l.getCourse().getId(), l.getCourse().getName(),
                l.getTitle(), l.getDescription(), l.getContent(),
                l.getFileUrl(), l.getVideoUrl(), l.getOrderNum(), l.getCreatedAt());
    }
}