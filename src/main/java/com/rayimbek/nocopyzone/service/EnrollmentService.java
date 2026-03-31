package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Course;
import com.rayimbek.nocopyzone.entity.Enrollment;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.CourseRepository;
import com.rayimbek.nocopyzone.repository.EnrollmentRepository;
import com.rayimbek.nocopyzone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Data
    @AllArgsConstructor
    public static class EnrollmentResponse {
        private Long id;
        private Long courseId;
        private String courseName;
        private String teacherName;
        private String studentName;
        private String studentEmail;
        private String enrolledAt;
    }

    // Student o'zi yoziladi
    @Transactional
    public EnrollmentResponse enroll(Long courseId, User student) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new RuntimeException("Siz allaqachon bu kursga yozilgansiz");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi: " + courseId));
        return toResponse(save(student, course));
    }

    // Teacher/Admin student biriktiradi
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

    @Transactional
    public void unenroll(Long courseId, User student) {
        Enrollment e = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Siz bu kursga yozilmagansiz"));
        enrollmentRepository.delete(e);
    }

    @Transactional
    public void unenrollById(Long courseId, Long studentId) {
        Enrollment e = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Talaba bu kursga yozilmagan"));
        enrollmentRepository.delete(e);
    }

    public List<EnrollmentResponse> getMyEnrollments(User student) {
        return enrollmentRepository.findByStudentId(student.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getCourseStudents(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId)
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
                e.getStudent().getFullName(),
                e.getStudent().getEmail(),
                e.getEnrolledAt().toString());
    }
}