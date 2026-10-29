package com.smartstudy.service;

import com.smartstudy.dto.workload.WorkloadResponse;
import com.smartstudy.dto.workload.WorkloadTrendItem;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.WorkloadAnalysis;
import com.smartstudy.entity.enums.RiskCategory;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import com.smartstudy.repository.WorkloadAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkloadAnalysisService {

    private final WorkloadAnalysisRepository workloadRepository;
    private final StudyTaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;

    public WorkloadAnalysisService(WorkloadAnalysisRepository workloadRepository,
                                   StudyTaskRepository taskRepository,
                                   UserRepository userRepository,
                                   UserPrivacySettingsRepository privacySettingsRepository) {
        this.workloadRepository = workloadRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.privacySettingsRepository = privacySettingsRepository;
    }

    @Transactional
    public WorkloadResponse calculateWorkload(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId).orElse(null);
        int dailyGoal = (settings != null && settings.getDailyStudyGoalMinutes() != null)
                ? settings.getDailyStudyGoalMinutes() : 240;
        int weeklyCapacity = dailyGoal * 7;

        List<StudyTask> allTasks = taskRepository.findByUserId(userId);

        int totalTasks = allTasks.size();
        int pendingTasks = 0;
        int completedTasks = 0;
        int missedTasks = 0;
        int totalEstimatedMinutes = 0;

        double weightedMinutesLoad = 0.0;
        LocalDateTime now = LocalDateTime.now();

        for (StudyTask task : allTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                completedTasks++;
            } else {
                pendingTasks++;
                int remainingMinutes = Math.max(0, task.getEstimatedMinutes() - (task.getActualMinutes() != null ? task.getActualMinutes() : 0));
                totalEstimatedMinutes += remainingMinutes;

                // Check for overdue / missed deadlines
                if (task.getDeadline() != null && task.getDeadline().isBefore(now)) {
                    missedTasks++;
                    weightedMinutesLoad += remainingMinutes * 2.2; // Extra heavy weight for overdue work
                } else if (task.getDeadline() != null) {
                    long hoursUntilDeadline = ChronoUnit.HOURS.between(now, task.getDeadline());
                    double proximityFactor;
                    if (hoursUntilDeadline <= 24) {
                        proximityFactor = 2.0;
                    } else if (hoursUntilDeadline <= 72) {
                        proximityFactor = 1.5;
                    } else if (hoursUntilDeadline <= 168) {
                        proximityFactor = 1.2;
                    } else {
                        proximityFactor = 0.8;
                    }

                    double difficultyFactor = getDifficultyMultiplier(task.getDifficulty());
                    weightedMinutesLoad += (remainingMinutes * difficultyFactor * proximityFactor);
                } else {
                    weightedMinutesLoad += remainingMinutes;
                }
            }
        }

        // Calculate load score relative to weekly capacity
        double loadRatio = weeklyCapacity > 0 ? (weightedMinutesLoad / weeklyCapacity) * 80.0 : 0.0;
        double missedPenalty = missedTasks * 10.0;
        double rawScore = loadRatio + missedPenalty;

        // Clamp between 0.0 and 100.0
        double finalScore = Math.min(100.0, Math.max(0.0, Math.round(rawScore * 10.0) / 10.0));

        RiskCategory riskCategory;
        if (finalScore <= 35.0) {
            riskCategory = RiskCategory.LOW;
        } else if (finalScore <= 65.0) {
            riskCategory = RiskCategory.MODERATE;
        } else if (finalScore <= 85.0) {
            riskCategory = RiskCategory.HIGH;
        } else {
            riskCategory = RiskCategory.SEVERE;
        }

        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Capacity Base: ").append(weeklyCapacity).append(" mins/wk; ");
        breakdown.append("Pending Workload: ").append(totalEstimatedMinutes).append(" mins; ");
        breakdown.append("Weighted Load: ").append(String.format("%.1f", weightedMinutesLoad)).append(" mins; ");
        breakdown.append("Missed Tasks Penalty: +").append(String.format("%.1f", missedPenalty)).append("; ");
        breakdown.append("Raw Load Index: ").append(String.format("%.1f", finalScore));

        StringBuilder explanation = new StringBuilder();
        explanation.append("Workload Evaluation for ").append(LocalDate.now()).append(":\n");
        explanation.append("Your current workload score is ").append(finalScore).append("/100 (").append(riskCategory).append("). ");
        if (riskCategory == RiskCategory.LOW) {
            explanation.append("Your workload is well-distributed within your normal weekly study capacity.");
        } else if (riskCategory == RiskCategory.MODERATE) {
            explanation.append("Your study load is manageable, but multiple tasks require consistent attention.");
        } else if (riskCategory == RiskCategory.HIGH) {
            explanation.append("Approaching peak study demand. High concentration of pending tasks near deadlines.");
        } else {
            explanation.append("Severe study bottleneck detected. Estimated task volume significantly exceeds your weekly study goal.");
        }
        if (missedTasks > 0) {
            explanation.append(" You have ").append(missedTasks).append(" overdue task(s) adding strain to your schedule.");
        }

        LocalDate today = LocalDate.now();
        Optional<WorkloadAnalysis> existingOpt = workloadRepository.findByUserIdAndAnalysisDate(userId, today);
        WorkloadAnalysis analysis;

        if (existingOpt.isPresent()) {
            analysis = existingOpt.get();
            analysis.setTotalTasks(totalTasks);
            analysis.setPendingTasks(pendingTasks);
            analysis.setCompletedTasks(completedTasks);
            analysis.setMissedTasks(missedTasks);
            analysis.setTotalEstimatedMinutes(totalEstimatedMinutes);
            analysis.setDailyAvailableMinutes(dailyGoal);
            analysis.setWorkloadScore(finalScore);
            analysis.setRiskCategory(riskCategory);
            analysis.setFactorBreakdown(breakdown.toString());
        } else {
            analysis = new WorkloadAnalysis(
                    user,
                    today,
                    totalTasks,
                    pendingTasks,
                    completedTasks,
                    missedTasks,
                    totalEstimatedMinutes,
                    dailyGoal,
                    finalScore,
                    riskCategory,
                    breakdown.toString()
            );
        }

        WorkloadAnalysis saved = workloadRepository.save(analysis);
        return WorkloadResponse.fromEntity(saved, explanation.toString());
    }

    @Transactional(readOnly = true)
    public WorkloadResponse getCurrentWorkload(Long userId) {
        WorkloadAnalysis latest = workloadRepository.findTopByUserIdOrderByAnalysisDateDesc(userId)
                .orElse(null);

        if (latest == null || !latest.getAnalysisDate().equals(LocalDate.now())) {
            return calculateWorkload(userId);
        }

        return WorkloadResponse.fromEntity(latest, latest.getFactorBreakdown());
    }

    @Transactional(readOnly = true)
    public List<WorkloadTrendItem> getWorkloadTrends(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days > 0 ? days : 14);
        LocalDate end = LocalDate.now();

        List<WorkloadAnalysis> list = workloadRepository.findByUserIdAndAnalysisDateBetweenOrderByAnalysisDateAsc(userId, start, end);
        List<WorkloadTrendItem> trends = new ArrayList<>();
        for (WorkloadAnalysis wa : list) {
            trends.add(new WorkloadTrendItem(
                    wa.getAnalysisDate(),
                    wa.getWorkloadScore(),
                    wa.getRiskCategory(),
                    wa.getPendingTasks()
            ));
        }
        return trends;
    }

    private double getDifficultyMultiplier(TaskDifficulty difficulty) {
        if (difficulty == null) return 1.3;
        switch (difficulty) {
            case HARD:
                return 1.6;
            case MEDIUM:
                return 1.3;
            case EASY:
            default:
                return 1.0;
        }
    }
}
