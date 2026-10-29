package com.smartstudy.dto.plan;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class StudyPlanRequest {

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Min(value = 30, message = "Daily available minutes must be at least 30")
    @Max(value = 720, message = "Daily available minutes cannot exceed 720 (12 hours)")
    private Integer dailyAvailableMinutes = 240;

    @Min(value = 25, message = "Study block minutes must be at least 25")
    @Max(value = 120, message = "Study block minutes cannot exceed 120")
    private Integer maxBlockMinutes = 50;

    @Min(value = 5, message = "Break minutes must be at least 5")
    @Max(value = 30, message = "Break minutes cannot exceed 30")
    private Integer breakMinutes = 10;

    public StudyPlanRequest() {
    }

    public StudyPlanRequest(LocalDate startDate, LocalDate endDate, Integer dailyAvailableMinutes,
                            Integer maxBlockMinutes, Integer breakMinutes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyAvailableMinutes = dailyAvailableMinutes != null ? dailyAvailableMinutes : 240;
        this.maxBlockMinutes = maxBlockMinutes != null ? maxBlockMinutes : 50;
        this.breakMinutes = breakMinutes != null ? breakMinutes : 10;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDailyAvailableMinutes() {
        return dailyAvailableMinutes;
    }

    public void setDailyAvailableMinutes(Integer dailyAvailableMinutes) {
        this.dailyAvailableMinutes = dailyAvailableMinutes;
    }

    public Integer getMaxBlockMinutes() {
        return maxBlockMinutes;
    }

    public void setMaxBlockMinutes(Integer maxBlockMinutes) {
        this.maxBlockMinutes = maxBlockMinutes;
    }

    public Integer getBreakMinutes() {
        return breakMinutes;
    }

    public void setBreakMinutes(Integer breakMinutes) {
        this.breakMinutes = breakMinutes;
    }
}
