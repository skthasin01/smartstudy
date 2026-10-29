package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.RiskCategory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workload_analyses", indexes = {
        @Index(name = "idx_workload_user_date", columnList = "user_id, analysis_date")
})
public class WorkloadAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "total_tasks", nullable = false)
    private Integer totalTasks;

    @Column(name = "pending_tasks", nullable = false)
    private Integer pendingTasks;

    @Column(name = "completed_tasks", nullable = false)
    private Integer completedTasks;

    @Column(name = "missed_tasks", nullable = false)
    private Integer missedTasks;

    @Column(name = "total_estimated_minutes", nullable = false)
    private Integer totalEstimatedMinutes;

    @Column(name = "daily_available_minutes", nullable = false)
    private Integer dailyAvailableMinutes;

    @Column(name = "workload_score", nullable = false)
    private Double workloadScore; // 0.0 to 100.0

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false, length = 20)
    private RiskCategory riskCategory;

    @Column(name = "factor_breakdown", columnDefinition = "TEXT")
    private String factorBreakdown;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WorkloadAnalysis() {
    }

    public WorkloadAnalysis(User user, LocalDate analysisDate, Integer totalTasks,
                            Integer pendingTasks, Integer completedTasks, Integer missedTasks,
                            Integer totalEstimatedMinutes, Integer dailyAvailableMinutes,
                            Double workloadScore, RiskCategory riskCategory, String factorBreakdown) {
        this.user = user;
        this.analysisDate = analysisDate != null ? analysisDate : LocalDate.now();
        this.totalTasks = totalTasks;
        this.pendingTasks = pendingTasks;
        this.completedTasks = completedTasks;
        this.missedTasks = missedTasks;
        this.totalEstimatedMinutes = totalEstimatedMinutes;
        this.dailyAvailableMinutes = dailyAvailableMinutes;
        this.workloadScore = workloadScore;
        this.riskCategory = riskCategory;
        this.factorBreakdown = factorBreakdown;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.analysisDate == null) {
            this.analysisDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
