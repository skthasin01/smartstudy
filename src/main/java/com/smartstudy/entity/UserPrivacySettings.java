package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_privacy_settings")
public class UserPrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "allow_wellness_tracking", nullable = false)
    private boolean allowWellnessTracking = true;

    @Column(name = "share_anonymous_stats", nullable = false)
    private boolean shareAnonymousStats = false;

    @Column(name = "reminder_notifications_enabled", nullable = false)
    private boolean reminderNotificationsEnabled = true;

    @Column(name = "daily_study_goal_minutes", nullable = false)
    private Integer dailyStudyGoalMinutes = 240;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserPrivacySettings() {
    }

    public UserPrivacySettings(User user) {
        this.user = user;
        this.allowWellnessTracking = true;
        this.shareAnonymousStats = false;
        this.reminderNotificationsEnabled = true;
        this.dailyStudyGoalMinutes = 240;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
