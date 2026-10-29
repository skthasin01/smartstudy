package com.smartstudy.repository;

import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Optional<StudySession> findByUserIdAndStatus(Long userId, SessionStatus status);

    Optional<StudySession> findByIdAndUserId(Long id, Long userId);

    Page<StudySession> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

    List<StudySession> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<StudySession> findByUserIdAndStartTimeAfter(Long userId, LocalDateTime start);
}
