package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_plans", indexes = {
        @Index(name = "idx_study_plans_user_id", columnList = "user_id"),
        @Index(name = "idx_study_plans_current", columnList = "is_current")
})
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_name", nullable = false, length = 150)
    private String planName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_allocated_minutes", nullable = false)
    private Integer totalAllocatedMinutes = 0;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(mappedBy = "studyPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudyPlanItem> items = new ArrayList<>();

    public StudyPlan() {
    }

    public StudyPlan(User user, String planName, LocalDate startDate, LocalDate endDate,
                     Integer totalAllocatedMinutes, String explanation) {
        this.user = user;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAllocatedMinutes = totalAllocatedMinutes;
        this.explanation = explanation;
        this.isCurrent = true;
    }

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    public void addItem(StudyPlanItem item) {
        items.add(item);
        item.setStudyPlan(this);
    }

    public void removeItem(StudyPlanItem item) {
        items.remove(item);
        item.setStudyPlan(null);
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
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
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

    public List<StudyPlanItem> getItems() {
        return items;
    }

    public void setItems(List<StudyPlanItem> items) {
        this.items = items;
    }
}
