package com.smartstudy.dto.workload;

import com.smartstudy.entity.enums.RiskCategory;

import java.time.LocalDate;

public class WorkloadTrendItem {

    private LocalDate date;
    private Double score;
    private RiskCategory riskCategory;
    private Integer pendingTasks;

    public WorkloadTrendItem() {
    }

    public WorkloadTrendItem(LocalDate date, Double score, RiskCategory riskCategory, Integer pendingTasks) {
        this.date = date;
        this.score = score;
        this.riskCategory = riskCategory;
        this.pendingTasks = pendingTasks;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }

    public Integer getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Integer pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
}
