package com.smartstudy.repository;

import com.smartstudy.entity.BurnoutAssessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BurnoutAssessmentRepository extends JpaRepository<BurnoutAssessment, Long> {

    Optional<BurnoutAssessment> findTopByUserIdOrderByAssessmentDateDesc(Long userId);

    Optional<BurnoutAssessment> findByUserIdAndAssessmentDate(Long userId, LocalDate assessmentDate);

    List<BurnoutAssessment> findByUserIdAndAssessmentDateBetweenOrderByAssessmentDateAsc(Long userId, LocalDate start, LocalDate end);

    Page<BurnoutAssessment> findByUserIdOrderByAssessmentDateDesc(Long userId, Pageable pageable);
}
