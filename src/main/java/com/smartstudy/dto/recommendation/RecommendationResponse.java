package com.smartstudy.dto.recommendation;

import com.smartstudy.entity.Recommendation;
import com.smartstudy.entity.enums.RecommendationType;
import com.smartstudy.entity.enums.TaskPriority;

import java.time.LocalDateTime;

public class RecommendationResponse {

    private Long id;
    private Long assessmentId;
    private String title;
    private String message;
    private RecommendationType type;
    private TaskPriority priority;
    private boolean read;
    private boolean dismissed;
    private LocalDateTime createdAt;

    public RecommendationResponse() {
    }

    public static RecommendationResponse fromEntity(Recommendation r) {
        if (r == null) return null;

        RecommendationResponse res = new RecommendationResponse();
        res.setId(r.getId());
        if (r.getAssessment() != null) {
            res.setAssessmentId(r.getAssessment().getId());
        }
        res.setTitle(r.getTitle());
        res.setMessage(r.getMessage());
        res.setType(r.getType());
        res.setPriority(r.getPriority());
        res.setRead(r.isRead());
        res.setDismissed(r.isDismissed());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RecommendationType getType() {
        return type;
    }

    public void setType(RecommendationType type) {
        this.type = type;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
