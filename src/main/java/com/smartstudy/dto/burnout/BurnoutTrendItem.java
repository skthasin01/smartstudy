package com.smartstudy.dto.burnout;

import com.smartstudy.entity.enums.BurnoutRiskLevel;

import java.time.LocalDate;

public class BurnoutTrendItem {

    private LocalDate date;
    private Double riskScore;
    private BurnoutRiskLevel riskLevel;

    public BurnoutTrendItem() {
    }

    public BurnoutTrendItem(LocalDate date, Double riskScore, BurnoutRiskLevel riskLevel) {
        this.date = date;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
