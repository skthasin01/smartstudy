package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.MoodLevel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_checkins", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "checkin_date"}, name = "uk_checkins_user_date")
}, indexes = {
        @Index(name = "idx_checkins_user_date", columnList = "user_id, checkin_date")
})
public class DailyCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "stress_level", nullable = false)
    private Integer stressLevel; // 1 (lowest) to 5 (highest)

    @Column(name = "energy_level", nullable = false)
    private Integer energyLevel; // 1 (lowest) to 5 (highest)

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_level", nullable = false, length = 20)
    private MoodLevel moodLevel = MoodLevel.NEUTRAL;

    @Column(name = "sleep_hours", nullable = false)
    private Double sleepHours;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyCheckin() {
    }

    public DailyCheckin(User user, LocalDate checkinDate, Integer stressLevel,
                        Integer energyLevel, MoodLevel moodLevel, Double sleepHours, String notes) {
        this.user = user;
        this.checkinDate = checkinDate != null ? checkinDate : LocalDate.now();
        this.stressLevel = stressLevel;
        this.energyLevel = energyLevel;
        this.moodLevel = moodLevel != null ? moodLevel : MoodLevel.NEUTRAL;
        this.sleepHours = sleepHours;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.checkinDate == null) {
            this.checkinDate = LocalDate.now();
        }
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

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    public MoodLevel getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(MoodLevel moodLevel) {
        this.moodLevel = moodLevel;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
