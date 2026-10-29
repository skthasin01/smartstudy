package com.smartstudy.repository;

import com.smartstudy.entity.StudyPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    Optional<StudyPlan> findByUserIdAndIsCurrentTrue(Long userId);

    Optional<StudyPlan> findByIdAndUserId(Long id, Long userId);

    Page<StudyPlan> findByUserIdOrderByGeneratedAtDesc(Long userId, Pageable pageable);

    List<StudyPlan> findByUserIdAndIsCurrent(Long userId, boolean isCurrent);
}
