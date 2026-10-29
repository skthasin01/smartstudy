package com.smartstudy.dto.settings;

import com.smartstudy.entity.UserPrivacySettings;

import java.time.LocalDateTime;

public class PrivacySettingsResponse {

    private Long id;
    private boolean allowWellnessTracking;
    private boolean shareAnonymousStats;
    private boolean reminderNotificationsEnabled;
    private Integer dailyStudyGoalMinutes;
    private LocalDateTime updatedAt;

    public PrivacySettingsResponse() {
    }

    public static PrivacySettingsResponse fromEntity(UserPrivacySettings s) {
        if (s == null) return null;

        PrivacySettingsResponse res = new PrivacySettingsResponse();
        res.setId(s.getId());
        res.setAllowWellnessTracking(s.isAllowWellnessTracking());
        res.setShareAnonymousStats(s.isShareAnonymousStats());
        res.setReminderNotificationsEnabled(s.isReminderNotificationsEnabled());
        res.setDailyStudyGoalMinutes(s.getDailyStudyGoalMinutes());
        res.setUpdatedAt(s.getUpdatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
