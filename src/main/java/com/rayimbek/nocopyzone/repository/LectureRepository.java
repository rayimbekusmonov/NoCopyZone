package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByCourseIdOrderByOrderNum(Long courseId);
}