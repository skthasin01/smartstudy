package com.smartstudy.dto.session;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class EndSessionRequest {

    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 240, message = "Break duration cannot exceed 240 minutes")
    private Integer breakDurationMinutes = 0;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private boolean markTaskCompleted = false;

    public EndSessionRequest() {
    }

    public EndSessionRequest(Integer breakDurationMinutes, String notes, boolean markTaskCompleted) {
        this.breakDurationMinutes = breakDurationMinutes != null ? breakDurationMinutes : 0;
        this.notes = notes;
        this.markTaskCompleted = markTaskCompleted;
    }

    public Integer getBreakDurationMinutes() {
        return breakDurationMinutes;
    }

    public void setBreakDurationMinutes(Integer breakDurationMinutes) {
        this.breakDurationMinutes = breakDurationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isMarkTaskCompleted() {
        return markTaskCompleted;
    }

    public void setMarkTaskCompleted(boolean markTaskCompleted) {
        this.markTaskCompleted = markTaskCompleted;
    }
}
