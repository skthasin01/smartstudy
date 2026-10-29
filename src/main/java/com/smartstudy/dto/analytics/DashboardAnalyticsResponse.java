package com.smartstudy.dto.analytics;

import com.smartstudy.dto.auth.UserProfileResponse;
import com.smartstudy.dto.burnout.BurnoutAssessmentResponse;
import com.smartstudy.dto.recommendation.RecommendationResponse;
import com.smartstudy.dto.task.TaskResponse;
import com.smartstudy.dto.workload.WorkloadResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardAnalyticsResponse {

    private UserProfileResponse profile;
    private long totalTasksCount;
    private long pendingTasksCount;
    private long completedTasksCount;
    private long missedTasksCount;

    private List<TaskResponse> todayTasks = new ArrayList<>();
    private List<TaskResponse> upcomingDeadlines = new ArrayList<>();

    private int todayStudyMinutes;
    private int weekStudyMinutes;
    private Map<String, Integer> weeklyStudyDistribution = new HashMap<>();

    private WorkloadResponse currentWorkload;
    private BurnoutAssessmentResponse currentBurnout;

    private List<RecommendationResponse> recentRecommendations = new ArrayList<>();
    private long unreadNotificationsCount;

    public DashboardAnalyticsResponse() {
    }

    public UserProfileResponse getProfile() {
        return profile;
    }

    public void setProfile(UserProfileResponse profile) {
        this.profile = profile;
    }

    public long getTotalTasksCount() {
        return totalTasksCount;
    }

    public void setTotalTasksCount(long totalTasksCount) {
        this.totalTasksCount = totalTasksCount;
    }

    public long getPendingTasksCount() {
        return pendingTasksCount;
    }

    public void setPendingTasksCount(long pendingTasksCount) {
        this.pendingTasksCount = pendingTasksCount;
    }

    public long getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void setCompletedTasksCount(long completedTasksCount) {
        this.completedTasksCount = completedTasksCount;
    }

    public long getMissedTasksCount() {
        return missedTasksCount;
    }

    public void setMissedTasksCount(long missedTasksCount) {
        this.missedTasksCount = missedTasksCount;
    }

    public List<TaskResponse> getTodayTasks() {
        return todayTasks;
    }

    public void setTodayTasks(List<TaskResponse> todayTasks) {
        this.todayTasks = todayTasks;
    }

    public List<TaskResponse> getUpcomingDeadlines() {
        return upcomingDeadlines;
    }

    public void setUpcomingDeadlines(List<TaskResponse> upcomingDeadlines) {
        this.upcomingDeadlines = upcomingDeadlines;
    }

    public int getTodayStudyMinutes() {
        return todayStudyMinutes;
    }

    public void setTodayStudyMinutes(int todayStudyMinutes) {
        this.todayStudyMinutes = todayStudyMinutes;
    }

    public int getWeekStudyMinutes() {
        return weekStudyMinutes;
    }

    public void setWeekStudyMinutes(int weekStudyMinutes) {
        this.weekStudyMinutes = weekStudyMinutes;
    }

    public Map<String, Integer> getWeeklyStudyDistribution() {
        return weeklyStudyDistribution;
    }

    public void setWeeklyStudyDistribution(Map<String, Integer> weeklyStudyDistribution) {
        this.weeklyStudyDistribution = weeklyStudyDistribution;
    }

    public WorkloadResponse getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(WorkloadResponse currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public BurnoutAssessmentResponse getCurrentBurnout() {
        return currentBurnout;
    }

    public void setCurrentBurnout(BurnoutAssessmentResponse currentBurnout) {
        this.currentBurnout = currentBurnout;
    }

    public List<RecommendationResponse> getRecentRecommendations() {
        return recentRecommendations;
    }

    public void setRecentRecommendations(List<RecommendationResponse> recentRecommendations) {
        this.recentRecommendations = recentRecommendations;
    }

    public long getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(long unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }
}
