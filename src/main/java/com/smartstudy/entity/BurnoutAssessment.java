package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.BurnoutRiskLevel;

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
@Table(name = "burnout_assessments", indexes = {
        @Index(name = "idx_burnout_user_date", columnList = "user_id, assessment_date")
})
public class BurnoutAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "risk_score", nullable = false)
    private Double riskScore; // 0.0 to 100.0

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private BurnoutRiskLevel riskLevel;

    @Column(name = "contributing_factors", columnDefinition = "TEXT")
    private String contributingFactors;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "recommendations_generated", nullable = false)
    private boolean recommendationsGenerated = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BurnoutAssessment() {
    }

    public BurnoutAssessment(User user, LocalDate assessmentDate, Double riskScore,
                             BurnoutRiskLevel riskLevel, String contributingFactors,
                             String explanation) {
        this.user = user;
        this.assessmentDate = assessmentDate != null ? assessmentDate : LocalDate.now();
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.contributingFactors = contributingFactors;
        this.explanation = explanation;
        this.recommendationsGenerated = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.assessmentDate == null) {
            this.assessmentDate = LocalDate.now();
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

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public BurnoutRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(BurnoutRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(String contributingFactors) {
        this.contributingFactors = contributingFactors;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isRecommendationsGenerated() {
        return recommendationsGenerated;
    }

    public void setRecommendationsGenerated(boolean recommendationsGenerated) {
        this.recommendationsGenerated = recommendationsGenerated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
