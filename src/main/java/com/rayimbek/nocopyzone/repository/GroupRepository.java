package com.rayimbek.nocopyzone.repository;

import com.rayimbek.nocopyzone.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    // Talabaning guruhlari
    @Query("SELECT g FROM Group g JOIN g.students s WHERE s.id = :studentId")
    List<Group> findByStudentId(@Param("studentId") Long studentId);

    // Kurs biriktirilgan guruhlar
    @Query("SELECT g FROM Group g JOIN g.courses c WHERE c.id = :courseId")
    List<Group> findByCourseId(@Param("courseId") Long courseId);
}