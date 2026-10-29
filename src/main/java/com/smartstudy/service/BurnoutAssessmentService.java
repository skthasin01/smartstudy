package com.smartstudy.service;

import com.smartstudy.dto.burnout.BurnoutAssessmentResponse;
import com.smartstudy.dto.burnout.BurnoutTrendItem;
import com.smartstudy.entity.BurnoutAssessment;
import com.smartstudy.entity.DailyCheckin;
import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.enums.BurnoutRiskLevel;
import com.smartstudy.entity.enums.MoodLevel;
import com.smartstudy.entity.enums.RiskCategory;
import com.smartstudy.entity.enums.SessionStatus;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.BurnoutAssessmentRepository;
import com.smartstudy.repository.DailyCheckinRepository;
import com.smartstudy.repository.StudySessionRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BurnoutAssessmentService {

    private final BurnoutAssessmentRepository burnoutRepository;
    private final UserRepository userRepository;
    private final StudyTaskRepository taskRepository;
    private final StudySessionRepository sessionRepository;
    private final DailyCheckinRepository checkinRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;
    private final WorkloadAnalysisService workloadService;
    private final RecommendationService recommendationService;

    public BurnoutAssessmentService(BurnoutAssessmentRepository burnoutRepository,
                                    UserRepository userRepository,
                                    StudyTaskRepository taskRepository,
                                    StudySessionRepository sessionRepository,
                                    DailyCheckinRepository checkinRepository,
                                    UserPrivacySettingsRepository privacySettingsRepository,
                                    WorkloadAnalysisService workloadService,
                                    @Lazy RecommendationService recommendationService) {
        this.burnoutRepository = burnoutRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.checkinRepository = checkinRepository;
        this.privacySettingsRepository = privacySettingsRepository;
        this.workloadService = workloadService;
        this.recommendationService = recommendationService;
    }

    @Transactional
    public BurnoutAssessmentResponse assessBurnout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId).orElse(null);
        boolean wellnessEnabled = settings == null || settings.isAllowWellnessTracking();

        // 1. Academic Workload Component (Base score from WorkloadAnalysis)
        double workloadScore = workloadService.getCurrentWorkload(userId).getWorkloadScore();

        // 2. Overdue / Missed Tasks Component
        LocalDateTime now = LocalDateTime.now();
        long missedTasksCount = taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(userId, now, TaskStatus.COMPLETED).size();

        // 3. Continuous Study Session Strain Component (Sessions in past 7 days)
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        List<StudySession> recentSessions = sessionRepository.findByUserIdAndStartTimeAfter(userId, sevenDaysAgo);

        int totalStudyMinutesRecent = 0;
        int longSessionsCount = 0; // Sessions >= 120 minutes with < 15 min break
        for (StudySession s : recentSessions) {
            if (s.getStatus() == SessionStatus.COMPLETED) {
                int duration = s.getActualDurationMinutes() != null ? s.getActualDurationMinutes() : 0;
                int breaks = s.getBreakDurationMinutes() != null ? s.getBreakDurationMinutes() : 0;
                totalStudyMinutesRecent += duration;

                if (duration >= 120 && breaks < 15) {
                    longSessionsCount++;
                }
            }
        }

        // 4. Wellness Data (if enabled and submitted in past 7 days)
        List<DailyCheckin> recentCheckins = checkinRepository.findByUserIdAndCheckinDateAfterOrderByCheckinDateAsc(userId, LocalDate.now().minusDays(7));
        boolean hasWellnessData = wellnessEnabled && !recentCheckins.isEmpty();

        double totalRiskPoints = 0.0;
        double maxPossiblePoints = 0.0;
        List<String> factorList = new ArrayList<>();

        // Workload Indicator (Weight: 40 points)
        maxPossiblePoints += 40.0;
        double workloadPoints = (workloadScore / 100.0) * 40.0;
        totalRiskPoints += workloadPoints;
        if (workloadScore > 65.0) {
            factorList.add("Elevated academic workload volume (" + String.format("%.1f", workloadScore) + "/100)");
        }

        // Overdue Tasks Indicator (Weight: 20 points)
        maxPossiblePoints += 20.0;
        double overduePoints = Math.min(20.0, missedTasksCount * 6.5);
        totalRiskPoints += overduePoints;
        if (missedTasksCount > 0) {
            factorList.add(missedTasksCount + " overdue task(s) creating schedule friction");
        }

        // Excessive Strain / Lack of Breaks (Weight: 15 points)
        maxPossiblePoints += 15.0;
        double strainPoints = Math.min(15.0, longSessionsCount * 5.0);
        totalRiskPoints += strainPoints;
        if (longSessionsCount > 0) {
            factorList.add(longSessionsCount + " intense session(s) logged without sufficient break time");
        }

        // Wellness Indicators (Weight: 25 points total if present)
        if (hasWellnessData) {
            maxPossiblePoints += 25.0;

            double avgStress = 0.0;
            double avgSleep = 0.0;
            double avgEnergy = 0.0;
            for (DailyCheckin c : recentCheckins) {
                avgStress += c.getStressLevel();
                avgSleep += c.getSleepHours();
                avgEnergy += c.getEnergyLevel();
            }
            avgStress /= recentCheckins.size();
            avgSleep /= recentCheckins.size();
            avgEnergy /= recentCheckins.size();

            // Stress contribution (up to 10 points)
            if (avgStress >= 4.0) {
                totalRiskPoints += 10.0;
                factorList.add("High self-reported stress levels (avg " + String.format("%.1f", avgStress) + "/5)");
            } else if (avgStress >= 3.0) {
                totalRiskPoints += 5.0;
            }

            // Sleep deprivation contribution (up to 10 points)
            if (avgSleep < 5.5) {
                totalRiskPoints += 10.0;
                factorList.add("Low sleep duration detected (avg " + String.format("%.1f", avgSleep) + " hrs/night)");
            } else if (avgSleep < 6.5) {
                totalRiskPoints += 5.0;
            }

            // Low energy / mood contribution (up to 5 points)
            if (avgEnergy <= 2.0) {
                totalRiskPoints += 5.0;
                factorList.add("Persistent low energy levels noted in recent check-ins");
            }
        }

        // Normalize to documented 0.0 - 100.0 scale
        double normalizedScore = maxPossiblePoints > 0 ? (totalRiskPoints / maxPossiblePoints) * 100.0 : 0.0;
        double finalRiskScore = Math.min(100.0, Math.max(0.0, Math.round(normalizedScore * 10.0) / 10.0));

        BurnoutRiskLevel riskLevel;
        if (finalRiskScore < 40.0) {
            riskLevel = BurnoutRiskLevel.LOW;
        } else if (finalRiskScore < 70.0) {
            riskLevel = BurnoutRiskLevel.MODERATE;
        } else {
            riskLevel = BurnoutRiskLevel.HIGH;
        }

        StringBuilder explanation = new StringBuilder();
        explanation.append("Burnout Risk Early-Warning Assessment (").append(LocalDate.now()).append("):\n");
        explanation.append("Your computed burnout index is ").append(finalRiskScore).append("/100, which reflects a ")
                .append(riskLevel).append(" risk level.\n");

        if (riskLevel == BurnoutRiskLevel.LOW) {
            explanation.append("Current study patterns appear balanced. Continue maintaining consistent study intervals and healthy rest habits.\n");
        } else if (riskLevel == BurnoutRiskLevel.MODERATE) {
            explanation.append("Signs of emerging academic fatigue or deadline compression detected. Consider incorporating scheduled pauses and reviewing upcoming commitments.\n");
        } else {
            explanation.append("Substantial burnout vulnerability identified. High task volume combined with tight deadlines poses risk of cognitive exhaustion. We advise restructuring your study plan and taking proactive rest.\n");
        }

        explanation.append("\nNote: This assessment is an academic productivity and pacing tool, not a medical or psychiatric diagnosis.");

        String factorsText = factorList.isEmpty()
                ? "No significant strain factors currently identified."
                : String.join("; ", factorList);

        LocalDate today = LocalDate.now();
        Optional<BurnoutAssessment> existingOpt = burnoutRepository.findByUserIdAndAssessmentDate(userId, today);
        BurnoutAssessment assessment;

        if (existingOpt.isPresent()) {
            assessment = existingOpt.get();
            assessment.setRiskScore(finalRiskScore);
            assessment.setRiskLevel(riskLevel);
            assessment.setContributingFactors(factorsText);
            assessment.setExplanation(explanation.toString());
        } else {
            assessment = new BurnoutAssessment(
                    user,
                    today,
                    finalRiskScore,
                    riskLevel,
                    factorsText,
                    explanation.toString()
            );
        }

        BurnoutAssessment saved = burnoutRepository.save(assessment);

        // Generate supportive recommendations based on the findings
        recommendationService.generateRecommendationsForAssessment(saved);
        saved.setRecommendationsGenerated(true);
        burnoutRepository.save(saved);

        return BurnoutAssessmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public BurnoutAssessmentResponse getCurrentAssessment(Long userId) {
        BurnoutAssessment latest = burnoutRepository.findTopByUserIdOrderByAssessmentDateDesc(userId)
                .orElse(null);

        if (latest == null || !latest.getAssessmentDate().equals(LocalDate.now())) {
            return assessBurnout(userId);
        }

        return BurnoutAssessmentResponse.fromEntity(latest);
    }

    @Transactional(readOnly = true)
    public List<BurnoutTrendItem> getBurnoutTrends(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days > 0 ? days : 14);
        LocalDate end = LocalDate.now();

        List<BurnoutAssessment> list = burnoutRepository.findByUserIdAndAssessmentDateBetweenOrderByAssessmentDateAsc(userId, start, end);
        List<BurnoutTrendItem> trends = new ArrayList<>();
        for (BurnoutAssessment ba : list) {
            trends.add(new BurnoutTrendItem(
                    ba.getAssessmentDate(),
                    ba.getRiskScore(),
                    ba.getRiskLevel()
            ));
        }
        return trends;
    }
}
