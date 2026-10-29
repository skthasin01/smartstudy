package com.smartstudy.repository;

import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long>, JpaSpecificationExecutor<StudyTask> {

    Optional<StudyTask> findByIdAndUserId(Long id, Long userId);

    List<StudyTask> findByUserId(Long userId);

    Page<StudyTask> findByUserId(Long userId, Pageable pageable);

    List<StudyTask> findByUserIdAndStatus(Long userId, TaskStatus status);

    List<StudyTask> findByUserIdAndStatusNot(Long userId, TaskStatus status);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, TaskStatus status);

    List<StudyTask> findByUserIdAndDeadlineBeforeAndStatusNot(Long userId, LocalDateTime deadline, TaskStatus status);

    List<StudyTask> findByUserIdAndDeadlineBetweenAndStatusNot(Long userId, LocalDateTime start, LocalDateTime end, TaskStatus status);

    List<StudyTask> findByDeadlineBeforeAndStatusNot(LocalDateTime deadline, TaskStatus status);

    List<StudyTask> findByDeadlineBetweenAndStatusNot(LocalDateTime start, LocalDateTime end, TaskStatus status);
}
