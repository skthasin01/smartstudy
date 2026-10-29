package com.smartstudy.dto.checkin;

import com.smartstudy.entity.enums.MoodLevel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class DailyCheckinRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkinDate;

    @NotNull(message = "Stress level is required")
    @Min(value = 1, message = "Stress level must be between 1 (lowest) and 5 (highest)")
    @Max(value = 5, message = "Stress level must be between 1 (lowest) and 5 (highest)")
    private Integer stressLevel;

    @NotNull(message = "Energy level is required")
    @Min(value = 1, message = "Energy level must be between 1 (lowest) and 5 (highest)")
    @Max(value = 5, message = "Energy level must be between 1 (lowest) and 5 (highest)")
    private Integer energyLevel;

    private MoodLevel moodLevel = MoodLevel.NEUTRAL;

    @NotNull(message = "Sleep hours is required")
    @DecimalMin(value = "0.0", message = "Sleep hours cannot be negative")
    @DecimalMax(value = "24.0", message = "Sleep hours cannot exceed 24 hours")
    private Double sleepHours;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    public DailyCheckinRequest() {
    }

    public DailyCheckinRequest(LocalDate checkinDate, Integer stressLevel, Integer energyLevel,
                               MoodLevel moodLevel, Double sleepHours, String notes) {
        this.checkinDate = checkinDate;
        this.stressLevel = stressLevel;
        this.energyLevel = energyLevel;
        this.moodLevel = moodLevel != null ? moodLevel : MoodLevel.NEUTRAL;
        this.sleepHours = sleepHours;
        this.notes = notes;
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
}
