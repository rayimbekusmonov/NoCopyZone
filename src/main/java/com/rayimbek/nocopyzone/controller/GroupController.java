package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.service.CourseService;
import com.rayimbek.nocopyzone.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {

    private final GroupService groupService;

    // Guruh yaratish (admin/teacher)
    @PostMapping
    public ResponseEntity<GroupService.GroupResponse> create(
            @RequestBody GroupService.CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.create(request));
    }

    // Barcha guruhlar
    @GetMapping
    public ResponseEntity<List<GroupService.GroupResponse>> getAll() {
        return ResponseEntity.ok(groupService.getAll());
    }

    // Bitta guruh
    @GetMapping("/{id}")
    public ResponseEntity<GroupService.GroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getById(id));
    }

    // Guruhga talaba qo'shish
    @PostMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<GroupService.GroupResponse> addStudent(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.addStudent(groupId, studentId));
    }

    // Guruhdan talabani olib tashlash
    @DeleteMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<GroupService.GroupResponse> removeStudent(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.removeStudent(groupId, studentId));
    }

    // Guruhga kurs biriktirish
    @PostMapping("/{groupId}/courses/{courseId}")
    public ResponseEntity<GroupService.GroupResponse> assignCourse(
            @PathVariable Long groupId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(groupService.assignCourse(groupId, courseId));
    }

    // Guruhdan kursni olib tashlash
    @DeleteMapping("/{groupId}/courses/{courseId}")
    public ResponseEntity<GroupService.GroupResponse> removeCourse(
            @PathVariable Long groupId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(groupService.removeCourse(groupId, courseId));
    }

    // Guruh talabalari
    @GetMapping("/{groupId}/students")
    public ResponseEntity<List<GroupService.UserDto>> getStudents(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupStudents(groupId));
    }

    // Talabaning guruh kurslari (student o'z kurslarini ko'radi)
    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseService.Response>> getMyCourses(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(groupService.getCoursesForStudent(student.getId()));
    }
}