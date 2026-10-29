package com.smartstudy.service;

import com.smartstudy.dto.recommendation.RecommendationResponse;
import com.smartstudy.entity.BurnoutAssessment;
import com.smartstudy.entity.Recommendation;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.BurnoutRiskLevel;
import com.smartstudy.entity.enums.RecommendationType;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional
    public void generateRecommendationsForAssessment(BurnoutAssessment assessment) {
        User user = assessment.getUser();
        Double score = assessment.getRiskScore();
        BurnoutRiskLevel level = assessment.getRiskLevel();

        if (level == BurnoutRiskLevel.HIGH || score >= 70.0) {
            addIfNotExists(user, assessment,
                    "Schedule Immediate Rest Intervals",
                    "Your burnout score is currently in the high range. Plan at least 15-minute restorative breaks between all study blocks and avoid continuous cramming sessions.",
                    RecommendationType.BREAK,
                    TaskPriority.URGENT);

            addIfNotExists(user, assessment,
                    "Re-evaluate Non-Urgent Tasks",
                    "Consider deferring or breaking down low-priority tasks to relieve pressure before critical deadlines.",
                    RecommendationType.WORKLOAD,
                    TaskPriority.HIGH);

            addIfNotExists(user, assessment,
                    "Utilize Campus Academic & Wellness Support",
                    "If coursework feels overwhelming, speak with your academic advisor, course instructor, or campus counseling center for guidance and accommodations.",
                    RecommendationType.ACADEMIC_SUPPORT,
                    TaskPriority.MEDIUM);
        } else if (level == BurnoutRiskLevel.MODERATE || score >= 40.0) {
            addIfNotExists(user, assessment,
                    "Adopt the Pomodoro Technique (50m / 10m)",
                    "Maintain concentration and prevent fatigue by studying for 50 minutes followed by an intentional 10-minute break away from screens.",
                    RecommendationType.BREAK,
                    TaskPriority.MEDIUM);

            addIfNotExists(user, assessment,
                    "Generate a Balanced Study Schedule",
                    "Use the automated Study Plan Generator to evenly distribute upcoming assignments across available days.",
                    RecommendationType.SCHEDULE,
                    TaskPriority.MEDIUM);
        } else {
            addIfNotExists(user, assessment,
                    "Maintain Consistent Study Cadence",
                    "Your study rhythm is well-balanced. Keep logging sessions and daily check-ins to stay ahead of upcoming deadlines.",
                    RecommendationType.WELLNESS,
                    TaskPriority.LOW);
        }
    }

    private void addIfNotExists(User user, BurnoutAssessment assessment, String title,
                                String message, RecommendationType type, TaskPriority priority) {
        if (!recommendationRepository.existsByUserIdAndTitleAndIsDismissedFalse(user.getId(), title)) {
            Recommendation rec = new Recommendation(user, assessment, title, message, type, priority);
            recommendationRepository.save(rec);
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getActiveRecommendations(Long userId) {
        List<Recommendation> list = recommendationRepository.findByUserIdAndIsDismissedFalseOrderByCreatedAtDesc(userId);
        List<RecommendationResponse> responses = new ArrayList<>();
        for (Recommendation r : list) {
            responses.add(RecommendationResponse.fromEntity(r));
        }
        return responses;
    }

    @Transactional
    public RecommendationResponse markAsRead(Long userId, Long recommendationId) {
        Recommendation rec = recommendationRepository.findByIdAndUserId(recommendationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));

        rec.setRead(true);
        Recommendation saved = recommendationRepository.save(rec);
        return RecommendationResponse.fromEntity(saved);
    }

    @Transactional
    public void dismiss(Long userId, Long recommendationId) {
        Recommendation rec = recommendationRepository.findByIdAndUserId(recommendationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));

        rec.setDismissed(true);
        recommendationRepository.save(rec);
    }
}
