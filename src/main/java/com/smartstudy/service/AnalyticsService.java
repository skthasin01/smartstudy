package com.smartstudy.service;

import com.smartstudy.dto.analytics.DashboardAnalyticsResponse;
import com.smartstudy.dto.auth.UserProfileResponse;
import com.smartstudy.dto.burnout.BurnoutAssessmentResponse;
import com.smartstudy.dto.recommendation.RecommendationResponse;
import com.smartstudy.dto.session.StudySummaryResponse;
import com.smartstudy.dto.task.TaskResponse;
import com.smartstudy.dto.workload.WorkloadResponse;
import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.enums.SessionStatus;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.repository.StudySessionRepository;
import com.smartstudy.repository.StudyTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AnalyticsService {

    private final UserService userService;
    private final StudyTaskRepository taskRepository;
    private final StudySessionRepository sessionRepository;
    private final StudySessionService sessionService;
    private final WorkloadAnalysisService workloadService;
    private final BurnoutAssessmentService burnoutService;
    private final RecommendationService recommendationService;
    private final NotificationService notificationService;

    public AnalyticsService(UserService userService,
                            StudyTaskRepository taskRepository,
                            StudySessionRepository sessionRepository,
                            StudySessionService sessionService,
                            WorkloadAnalysisService workloadService,
                            BurnoutAssessmentService burnoutService,
                            RecommendationService recommendationService,
                            NotificationService notificationService) {
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
        this.workloadService = workloadService;
        this.burnoutService = burnoutService;
        this.recommendationService = recommendationService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public DashboardAnalyticsResponse getDashboardData(Long userId) {
        DashboardAnalyticsResponse dashboard = new DashboardAnalyticsResponse();

        // 1. User Profile
        UserProfileResponse profile = userService.getProfile(userId);
        dashboard.setProfile(profile);

        // 2. Task metrics
        List<StudyTask> allTasks = taskRepository.findByUserId(userId);
        long pending = 0;
        long completed = 0;
        long missed = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
        LocalDateTime inSevenDays = now.plusDays(7);

        List<TaskResponse> todayTasks = new ArrayList<>();
        List<TaskResponse> upcomingDeadlines = new ArrayList<>();

        for (StudyTask t : allTasks) {
            if (t.getStatus() == TaskStatus.COMPLETED) {
                completed++;
            } else {
                pending++;
                if (t.getDeadline() != null) {
                    if (t.getDeadline().isBefore(now)) {
                        missed++;
                    } else if (!t.getDeadline().isAfter(endOfToday)) {
                        todayTasks.add(TaskResponse.fromEntity(t));
                    }

                    if (t.getDeadline().isAfter(now) && !t.getDeadline().isAfter(inSevenDays)) {
                        upcomingDeadlines.add(TaskResponse.fromEntity(t));
                    }
                }
            }
        }

        dashboard.setTotalTasksCount(allTasks.size());
        dashboard.setPendingTasksCount(pending);
        dashboard.setCompletedTasksCount(completed);
        dashboard.setMissedTasksCount(missed);
        dashboard.setTodayTasks(todayTasks);
        dashboard.setUpcomingDeadlines(upcomingDeadlines);

        // 3. Study Sessions and Weekly Distribution
        StudySummaryResponse sessionSummary = sessionService.getSummary(userId);
        dashboard.setTodayStudyMinutes(sessionSummary.getTodayMinutes());
        dashboard.setWeekStudyMinutes(sessionSummary.getWeekMinutes());

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<StudySession> weekSessions = sessionRepository.findByUserIdAndStartTimeBetween(
                userId,
                monday.atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59)
        );

        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            String dayName = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            distribution.put(dayName, 0);
        }

        for (StudySession s : weekSessions) {
            if (s.getStatus() == SessionStatus.COMPLETED && s.getActualDurationMinutes() != null) {
                String dayName = s.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                Integer curr = distribution.get(dayName);
                if (curr != null) {
                    distribution.put(dayName, curr + s.getActualDurationMinutes());
                }
            }
        }
        dashboard.setWeeklyStudyDistribution(distribution);

        // 4. Workload Analysis
        WorkloadResponse workload = workloadService.getCurrentWorkload(userId);
        dashboard.setCurrentWorkload(workload);

        // 5. Burnout Assessment
        BurnoutAssessmentResponse burnout = burnoutService.getCurrentAssessment(userId);
        dashboard.setCurrentBurnout(burnout);

        // 6. Recommendations
        List<RecommendationResponse> recs = recommendationService.getActiveRecommendations(userId);
        dashboard.setRecentRecommendations(recs);

        // 7. Unread Notifications Count
        long unreadNotifs = notificationService.getUnreadCount(userId);
        dashboard.setUnreadNotificationsCount(unreadNotifs);

        return dashboard;
    }
}
