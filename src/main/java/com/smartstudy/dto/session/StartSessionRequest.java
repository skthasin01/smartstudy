package com.smartstudy.dto.session;

import javax.validation.constraints.Size;

public class StartSessionRequest {

    private Long taskId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public StartSessionRequest() {
    }

    public StartSessionRequest(Long taskId, String notes) {
        this.taskId = taskId;
        this.notes = notes;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
