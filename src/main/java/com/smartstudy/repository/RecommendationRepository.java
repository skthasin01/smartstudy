package com.smartstudy.repository;

import com.smartstudy.entity.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserIdAndIsDismissedFalseOrderByCreatedAtDesc(Long userId);

    Page<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Recommendation> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndTitleAndIsDismissedFalse(Long userId, String title);
}
