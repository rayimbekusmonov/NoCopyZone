package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String teacherName;
        private LocalDateTime createdAt;
    }

    @Data
    public static class CreateRequest {
        private String name;
        private String description;
    }

    @Transactional
    public Response create(CreateRequest request, User teacher) {
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        return toResponse(courseRepository.save(course));
    }

    public List<Response> getMyCourses(User teacher) {
        return courseRepository.findByTeacherId(teacher.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<Response> getAll() {
        return courseRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Response getById(Long id) {
        return toResponse(courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id)));
    }

    public Response toResponse(Course c) {
        return new Response(
                c.getId(), c.getName(), c.getDescription(),
                c.getTeacher().getFullName(), c.getCreatedAt());
    }
}