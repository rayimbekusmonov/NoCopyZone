package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.Group;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import com.rayimbek.nocopyzone.repository.GroupRepository;
import com.rayimbek.nocopyzone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Data
    @AllArgsConstructor
    public static class GroupResponse {
        private Long id;
        private String name;
        private String faculty;
        private Integer year;
        private int studentCount;
        private int courseCount;
    }

    @Data
    public static class CreateGroupRequest {
        private String name;
        private String faculty;
        private Integer year;
    }

    // Guruh yaratish
    @Transactional
    public GroupResponse create(CreateGroupRequest request) {
        if (groupRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Bu nom bilan guruh allaqachon mavjud: " + request.getName());
        }
        Group group = new Group();
        group.setName(request.getName());
        group.setFaculty(request.getFaculty());
        group.setYear(request.getYear());
        return toResponse(groupRepository.save(group));
    }

    // Guruhga talaba qo'shish
    @Transactional
    public GroupResponse addStudent(Long groupId, Long studentId) {
        Group group = getGroup(groupId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Talaba topilmadi: " + studentId));
        group.getStudents().add(student);
        return toResponse(groupRepository.save(group));
    }

    // Guruhdan talabani olib tashlash
    @Transactional
    public GroupResponse removeStudent(Long groupId, Long studentId) {
        Group group = getGroup(groupId);
        group.getStudents().removeIf(s -> s.getId().equals(studentId));
        return toResponse(groupRepository.save(group));
    }

    // Guruhga kurs biriktirish — shu guruh talabalari kursni avtomatik ko'radi
    @Transactional
    public GroupResponse assignCourse(Long groupId, Long courseId) {
        Group group = getGroup(groupId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi: " + courseId));
        group.getCourses().add(course);
        return toResponse(groupRepository.save(group));
    }

    // Guruhdan kursni olib tashlash
    @Transactional
    public GroupResponse removeCourse(Long groupId, Long courseId) {
        Group group = getGroup(groupId);
        group.getCourses().removeIf(c -> c.getId().equals(courseId));
        return toResponse(groupRepository.save(group));
    }

    // Barcha guruhlar
    public List<GroupResponse> getAll() {
        return groupRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Bitta guruh
    public GroupResponse getById(Long id) {
        return toResponse(getGroup(id));
    }

    // Talabaning guruh kurslari — student dashboard uchun
    @Transactional(readOnly = true)
    public List<CourseService.Response> getCoursesForStudent(Long studentId) {
        List<Group> groups = groupRepository.findByStudentId(studentId);
        return groups.stream()
                .flatMap(g -> g.getCourses().stream())
                .distinct()
                .map(c -> new CourseService.Response(
                        c.getId(), c.getName(), c.getDescription(),
                        c.getTeacher().getFullName(), c.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // Guruh talabalari
    @Transactional(readOnly = true)
    public List<UserDto> getGroupStudents(Long groupId) {
        Group group = getGroup(groupId);
        return group.getStudents().stream()
                .map(s -> new UserDto(s.getId(), s.getFullName(), s.getEmail()))
                .collect(Collectors.toList());
    }

    private Group getGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guruh topilmadi: " + id));
    }

    private GroupResponse toResponse(Group g) {
        return new GroupResponse(
                g.getId(), g.getName(), g.getFaculty(), g.getYear(),
                g.getStudents().size(), g.getCourses().size()
        );
    }

    @Data
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String fullName;
        private String email;
    }
}