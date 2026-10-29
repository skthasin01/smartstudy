package com.smartstudy.repository;

import com.smartstudy.entity.WorkloadAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadAnalysisRepository extends JpaRepository<WorkloadAnalysis, Long> {

    Optional<WorkloadAnalysis> findTopByUserIdOrderByAnalysisDateDesc(Long userId);

    Optional<WorkloadAnalysis> findByUserIdAndAnalysisDate(Long userId, LocalDate analysisDate);

    List<WorkloadAnalysis> findByUserIdAndAnalysisDateBetweenOrderByAnalysisDateAsc(Long userId, LocalDate start, LocalDate end);

    Page<WorkloadAnalysis> findByUserIdOrderByAnalysisDateDesc(Long userId, Pageable pageable);
}
