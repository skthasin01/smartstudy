package com.smartstudy.dto.checkin;

import com.smartstudy.entity.DailyCheckin;
import com.smartstudy.entity.enums.MoodLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyCheckinResponse {

    private Long id;
    private LocalDate checkinDate;
    private Integer stressLevel;
    private Integer energyLevel;
    private MoodLevel moodLevel;
    private String moodDescription;
    private Double sleepHours;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DailyCheckinResponse() {
    }

    public static DailyCheckinResponse fromEntity(DailyCheckin checkin) {
        if (checkin == null) return null;

        DailyCheckinResponse res = new DailyCheckinResponse();
        res.setId(checkin.getId());
        res.setCheckinDate(checkin.getCheckinDate());
        res.setStressLevel(checkin.getStressLevel());
        res.setEnergyLevel(checkin.getEnergyLevel());
        res.setMoodLevel(checkin.getMoodLevel());
        if (checkin.getMoodLevel() != null) {
            res.setMoodDescription(checkin.getMoodLevel().getDescription());
        }
        res.setSleepHours(checkin.getSleepHours());
        res.setNotes(checkin.getNotes());
        res.setCreatedAt(checkin.getCreatedAt());
        res.setUpdatedAt(checkin.getUpdatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMoodDescription() {
        return moodDescription;
    }

    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
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
