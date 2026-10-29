package com.smartstudy.dto.workload;

import com.smartstudy.entity.WorkloadAnalysis;
import com.smartstudy.entity.enums.RiskCategory;

import java.time.LocalDate;

public class WorkloadResponse {

    private LocalDate analysisDate;
    private Integer totalTasks;
    private Integer pendingTasks;
    private Integer completedTasks;
    private Integer missedTasks;
    private Integer totalEstimatedMinutes;
    private Integer dailyAvailableMinutes;
    private Double workloadScore;
    private RiskCategory riskCategory;
    private String factorBreakdown;
    private String explanation;

    public WorkloadResponse() {
    }

    public static WorkloadResponse fromEntity(WorkloadAnalysis analysis, String explanation) {
        if (analysis == null) return null;

        WorkloadResponse res = new WorkloadResponse();
        res.setAnalysisDate(analysis.getAnalysisDate());
        res.setTotalTasks(analysis.getTotalTasks());
        res.setPendingTasks(analysis.getPendingTasks());
        res.setCompletedTasks(analysis.getCompletedTasks());
        res.setMissedTasks(analysis.getMissedTasks());
        res.setTotalEstimatedMinutes(analysis.getTotalEstimatedMinutes());
        res.setDailyAvailableMinutes(analysis.getDailyAvailableMinutes());
        res.setWorkloadScore(analysis.getWorkloadScore());
        res.setRiskCategory(analysis.getRiskCategory());
        res.setFactorBreakdown(analysis.getFactorBreakdown());
        res.setExplanation(explanation);
        return res;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public Integer getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }

    public Integer getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Integer pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getMissedTasks() {
        return missedTasks;
    }

    public void setMissedTasks(Integer missedTasks) {
        this.missedTasks = missedTasks;
    }

    public Integer getTotalEstimatedMinutes() {
        return totalEstimatedMinutes;
    }

    public void setTotalEstimatedMinutes(Integer totalEstimatedMinutes) {
        this.totalEstimatedMinutes = totalEstimatedMinutes;
    }

    public Integer getDailyAvailableMinutes() {
        return dailyAvailableMinutes;
    }

    public void setDailyAvailableMinutes(Integer dailyAvailableMinutes) {
        this.dailyAvailableMinutes = dailyAvailableMinutes;
    }

    public Double getWorkloadScore() {
        return workloadScore;
    }

    public void setWorkloadScore(Double workloadScore) {
        this.workloadScore = workloadScore;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }

    public String getFactorBreakdown() {
        return factorBreakdown;
    }

    public void setFactorBreakdown(String factorBreakdown) {
        this.factorBreakdown = factorBreakdown;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
