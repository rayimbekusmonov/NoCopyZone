package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.dto.TaskDto;
import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.Task;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import com.rayimbek.nocopyzone.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public TaskDto.Response create(TaskDto.CreateRequest request, User teacher) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi: " + request.getCourseId()));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setCourse(course);
        task.setTeacher(teacher);
        task.setDeadline(request.getDeadline());
        task.setMaxScore(request.getMaxScore() != null ? request.getMaxScore() : 100);
        task.setDurationMinutes(request.getDurationMinutes());

        return toResponse(taskRepository.save(task));
    }

    public List<TaskDto.Response> getByCourse(Long courseId) {
        return taskRepository.findByCourseId(courseId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskDto.Response> getMyTasks(User teacher) {
        return taskRepository.findByTeacherId(teacher.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskDto.Response getById(Long id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task topilmadi: " + id)));
    }

    private TaskDto.Response toResponse(Task t) {
        return new TaskDto.Response(
                t.getId(), t.getTitle(), t.getDescription(), t.getType(),
                t.getCourse().getId(), t.getCourse().getName(),
                t.getTeacher().getFullName(),
                t.getDeadline(), t.getMaxScore(),
                t.getDurationMinutes(),
                t.getCreatedAt());
    }
}