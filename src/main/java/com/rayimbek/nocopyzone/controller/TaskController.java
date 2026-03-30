package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.dto.TaskDto;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto.Response> create(
            @Valid @RequestBody TaskDto.CreateRequest request,
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(taskService.create(request, teacher));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TaskDto.Response>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(taskService.getByCourse(courseId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskDto.Response>> getMyTasks(
            @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(taskService.getMyTasks(teacher));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }
}