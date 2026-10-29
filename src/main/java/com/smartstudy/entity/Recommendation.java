package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.RecommendationType;
import com.smartstudy.entity.enums.TaskPriority;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations", indexes = {
        @Index(name = "idx_recommendations_user_id", columnList = "user_id"),
        @Index(name = "idx_recommendations_status", columnList = "is_read, is_dismissed")
})
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private BurnoutAssessment assessment;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private RecommendationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "is_dismissed", nullable = false)
    private boolean isDismissed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Recommendation() {
    }

    public Recommendation(User user, BurnoutAssessment assessment, String title,
                          String message, RecommendationType type, TaskPriority priority) {
        this.user = user;
        this.assessment = assessment;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.isRead = false;
        this.isDismissed = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public BurnoutAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(BurnoutAssessment assessment) {
        this.assessment = assessment;
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
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDismissed() {
        return isDismissed;
    }

    public void setDismissed(boolean dismissed) {
        isDismissed = dismissed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
