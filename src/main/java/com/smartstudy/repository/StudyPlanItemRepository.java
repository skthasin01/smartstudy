package com.smartstudy.repository;

import com.smartstudy.entity.StudyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

    List<StudyPlanItem> findByStudyPlanIdOrderByScheduledDateAscStartTimeAsc(Long studyPlanId);

    List<StudyPlanItem> findByStudyPlanUserIdAndScheduledDateOrderByStartTimeAsc(Long userId, LocalDate scheduledDate);

    Optional<StudyPlanItem> findByIdAndStudyPlanUserId(Long id, Long userId);
}
