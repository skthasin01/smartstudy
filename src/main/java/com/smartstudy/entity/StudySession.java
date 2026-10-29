package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.SessionStatus;

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
@Table(name = "study_sessions", indexes = {
        @Index(name = "idx_study_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_study_sessions_status", columnList = "status"),
        @Index(name = "idx_study_sessions_start_time", columnList = "start_time")
})
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private StudyTask task;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes = 0;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "notes", length = 500)
    private String notes;

    public StudySession() {
    }

    public StudySession(User user, StudyTask task, LocalDateTime startTime, String notes) {
        this.user = user;
        this.task = task;
        this.startTime = startTime != null ? startTime : LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
        this.actualDurationMinutes = 0;
        this.breakDurationMinutes = 0;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
        if (this.actualDurationMinutes == null) {
            this.actualDurationMinutes = 0;
        }
        if (this.breakDurationMinutes == null) {
            this.breakDurationMinutes = 0;
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

    public StudyTask getTask() {
        return task;
    }

    public void setTask(StudyTask task) {
        this.task = task;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public Integer getBreakDurationMinutes() {
        return breakDurationMinutes;
    }

    public void setBreakDurationMinutes(Integer breakDurationMinutes) {
        this.breakDurationMinutes = breakDurationMinutes;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
