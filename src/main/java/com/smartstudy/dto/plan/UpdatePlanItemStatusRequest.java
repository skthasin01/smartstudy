package com.smartstudy.dto.plan;

import com.smartstudy.entity.enums.PlanItemStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdatePlanItemStatusRequest {

    @NotNull(message = "Plan item status is required")
    private PlanItemStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public UpdatePlanItemStatusRequest() {
    }

    public UpdatePlanItemStatusRequest(PlanItemStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public PlanItemStatus getStatus() {
        return status;
    }

    public void setStatus(PlanItemStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
