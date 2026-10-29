package com.smartstudy.dto.plan;

import com.smartstudy.entity.StudyPlan;
import com.smartstudy.entity.StudyPlanItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyPlanResponse {

    private Long id;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalAllocatedMinutes;
    private boolean current;
    private LocalDateTime generatedAt;
    private String explanation;
    private List<StudyPlanItemResponse> items = new ArrayList<>();

    public StudyPlanResponse() {
    }

    public static StudyPlanResponse fromEntity(StudyPlan plan) {
        if (plan == null) return null;

        StudyPlanResponse res = new StudyPlanResponse();
        res.setId(plan.getId());
        res.setPlanName(plan.getPlanName());
        res.setStartDate(plan.getStartDate());
        res.setEndDate(plan.getEndDate());
        res.setTotalAllocatedMinutes(plan.getTotalAllocatedMinutes());
        res.setCurrent(plan.isCurrent());
        res.setGeneratedAt(plan.getGeneratedAt());
        res.setExplanation(plan.getExplanation());

        if (plan.getItems() != null) {
            for (StudyPlanItem item : plan.getItems()) {
                res.getItems().add(StudyPlanItemResponse.fromEntity(item));
            }
        }
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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

    public Integer getTotalAllocatedMinutes() {
        return totalAllocatedMinutes;
    }

    public void setTotalAllocatedMinutes(Integer totalAllocatedMinutes) {
        this.totalAllocatedMinutes = totalAllocatedMinutes;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<StudyPlanItemResponse> getItems() {
        return items;
    }

    public void setItems(List<StudyPlanItemResponse> items) {
        this.items = items;
    }
}
