package com.smartstudy.dto.settings;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PrivacySettingsRequest {

    private boolean allowWellnessTracking = true;
    private boolean shareAnonymousStats = false;
    private boolean reminderNotificationsEnabled = true;

    @Min(value = 30, message = "Daily study goal must be at least 30 minutes")
    @Max(value = 720, message = "Daily study goal cannot exceed 720 minutes (12 hours)")
    private Integer dailyStudyGoalMinutes = 240;

    public PrivacySettingsRequest() {
    }

    public PrivacySettingsRequest(boolean allowWellnessTracking, boolean shareAnonymousStats,
                                  boolean reminderNotificationsEnabled, Integer dailyStudyGoalMinutes) {
        this.allowWellnessTracking = allowWellnessTracking;
        this.shareAnonymousStats = shareAnonymousStats;
        this.reminderNotificationsEnabled = reminderNotificationsEnabled;
        this.dailyStudyGoalMinutes = dailyStudyGoalMinutes != null ? dailyStudyGoalMinutes : 240;
    }

    public boolean isAllowWellnessTracking() {
        return allowWellnessTracking;
    }

    public void setAllowWellnessTracking(boolean allowWellnessTracking) {
        this.allowWellnessTracking = allowWellnessTracking;
    }

    public boolean isShareAnonymousStats() {
        return shareAnonymousStats;
    }

    public void setShareAnonymousStats(boolean shareAnonymousStats) {
        this.shareAnonymousStats = shareAnonymousStats;
    }

    public boolean isReminderNotificationsEnabled() {
        return reminderNotificationsEnabled;
    }

    public void setReminderNotificationsEnabled(boolean reminderNotificationsEnabled) {
        this.reminderNotificationsEnabled = reminderNotificationsEnabled;
    }

    public Integer getDailyStudyGoalMinutes() {
        return dailyStudyGoalMinutes;
    }

    public void setDailyStudyGoalMinutes(Integer dailyStudyGoalMinutes) {
        this.dailyStudyGoalMinutes = dailyStudyGoalMinutes;
    }
}
