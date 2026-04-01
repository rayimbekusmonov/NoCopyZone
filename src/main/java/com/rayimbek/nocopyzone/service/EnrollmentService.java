package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.Enrollment;
import com.rayimbek.nocopyzone.entity.Group;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import com.rayimbek.nocopyzone.repository.EnrollmentRepository;
import com.rayimbek.nocopyzone.repository.GroupRepository;
import com.rayimbek.nocopyzone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Data
    @AllArgsConstructor
    public static class EnrollmentResponse {
        private Long id;
        private Long courseId;
        private String courseName;
        private String teacherName;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private String enrolledAt;
    }

    @Data
    @AllArgsConstructor
    public static class GroupEnrollResult {
        private int enrolled;   // qo'shilganlar soni
        private int skipped;    // allaqachon bor edi
        private String groupName;
    }

    // Teacher/Admin — alohida talaba qo'shish
    @Transactional
    public EnrollmentResponse enrollById(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Bu talaba allaqachon kursga yozilgan");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Talaba topilmadi"));
        return toResponse(save(student, course));
    }

    // Teacher/Admin — butun guruhni kursga biriktirish
    @Transactional
    public GroupEnrollResult enrollGroup(Long courseId, Long groupId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Guruh topilmadi"));

        int enrolled = 0;
        int skipped = 0;

        for (User student : group.getStudents()) {
            if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
                skipped++;
            } else {
                save(student, course);
                enrolled++;
            }
        }

        return new GroupEnrollResult(enrolled, skipped, group.getName());
    }

    // Talabani kursdan chiqarish
    @Transactional
    public void unenrollById(Long courseId, Long studentId) {
        Enrollment e = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Talaba bu kursga yozilmagan"));
        enrollmentRepository.delete(e);
    }

    // Kurs talabalari ro'yxati
    public List<EnrollmentResponse> getCourseStudents(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Student o'z kurslarini ko'radi (guruh orqali)
    public List<EnrollmentResponse> getMyEnrollments(User student) {
        return enrollmentRepository.findByStudentId(student.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Enrollment save(User student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        return enrollmentRepository.save(enrollment);
    }

    private EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getCourse().getName(),
                e.getCourse().getTeacher().getFullName(),
                e.getStudent().getId(),
                e.getStudent().getFullName(),
                e.getStudent().getEmail(),
                e.getEnrolledAt().toString());
    }
}