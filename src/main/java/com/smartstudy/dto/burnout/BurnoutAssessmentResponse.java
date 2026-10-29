package com.smartstudy.dto.burnout;

import com.smartstudy.entity.BurnoutAssessment;
import com.smartstudy.entity.enums.BurnoutRiskLevel;

import java.time.LocalDate;

public class BurnoutAssessmentResponse {

    private Long id;
    private LocalDate assessmentDate;
    private Double riskScore;
    private BurnoutRiskLevel riskLevel;
    private String contributingFactors;
    private String explanation;
    private boolean recommendationsGenerated;

    public BurnoutAssessmentResponse() {
    }

    public static BurnoutAssessmentResponse fromEntity(BurnoutAssessment assessment) {
        if (assessment == null) return null;

        BurnoutAssessmentResponse res = new BurnoutAssessmentResponse();
        res.setId(assessment.getId());
        res.setAssessmentDate(assessment.getAssessmentDate());
        res.setRiskScore(assessment.getRiskScore());
        res.setRiskLevel(assessment.getRiskLevel());
        res.setContributingFactors(assessment.getContributingFactors());
        res.setExplanation(assessment.getExplanation());
        res.setRecommendationsGenerated(assessment.isRecommendationsGenerated());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
