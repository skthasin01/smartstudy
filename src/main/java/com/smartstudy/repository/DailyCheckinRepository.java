package com.smartstudy.repository;

import com.smartstudy.entity.DailyCheckin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyCheckinRepository extends JpaRepository<DailyCheckin, Long> {

    Optional<DailyCheckin> findByUserIdAndCheckinDate(Long userId, LocalDate checkinDate);

    Optional<DailyCheckin> findByIdAndUserId(Long id, Long userId);

    Page<DailyCheckin> findByUserIdOrderByCheckinDateDesc(Long userId, Pageable pageable);

    List<DailyCheckin> findByUserIdAndCheckinDateBetweenOrderByCheckinDateAsc(Long userId, LocalDate start, LocalDate end);

    List<DailyCheckin> findByUserIdAndCheckinDateAfterOrderByCheckinDateAsc(Long userId, LocalDate date);

    void deleteByUserId(Long userId);
}
