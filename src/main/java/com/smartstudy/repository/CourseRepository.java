package com.smartstudy.repository;

import com.smartstudy.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserIdOrderByCourseCodeAsc(Long userId);
    Optional<Course> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndCourseCodeIgnoreCase(Long userId, String courseCode);
}
