package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.dto.CourseDto;
import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public CourseDto.Response create(CourseDto.CreateRequest request, User teacher) {
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        return toResponse(courseRepository.save(course));
    }

    public List<CourseDto.Response> getMyCoures(User teacher) {
        return courseRepository.findByTeacherId(teacher.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CourseDto.Response> getAll() {
        return courseRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CourseDto.Response getById(Long id) {
        return toResponse(courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id)));
    }

    private CourseDto.Response toResponse(Course c) {
        return new CourseDto.Response(
                c.getId(), c.getName(), c.getDescription(),
                c.getTeacher().getFullName(), c.getCreatedAt());
    }
}