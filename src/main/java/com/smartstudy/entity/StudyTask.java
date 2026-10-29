package com.smartstudy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;

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
@Table(name = "study_tasks", indexes = {
        @Index(name = "idx_study_tasks_user_id", columnList = "user_id"),
        @Index(name = "idx_study_tasks_status", columnList = "status"),
        @Index(name = "idx_study_tasks_deadline", columnList = "deadline")
})
public class StudyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 30)
    private TaskType taskType = TaskType.ASSIGNMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private TaskDifficulty difficulty = TaskDifficulty.MEDIUM;

    @Column(name = "estimated_minutes", nullable = false)
    private Integer estimatedMinutes;

    @Column(name = "actual_minutes")
    private Integer actualMinutes = 0;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public StudyTask() {
    }

    public StudyTask(User user, Course course, String title, String description,
                     TaskType taskType, TaskDifficulty difficulty, Integer estimatedMinutes,
                     LocalDateTime deadline, TaskPriority priority) {
        this.user = user;
        this.course = course;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.difficulty = difficulty;
        this.estimatedMinutes = estimatedMinutes;
        this.deadline = deadline;
        this.priority = priority;
        this.status = TaskStatus.TODO;
        this.actualMinutes = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.actualMinutes == null) {
            this.actualMinutes = 0;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(TaskDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Integer getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(Integer actualMinutes) {
        this.actualMinutes = actualMinutes;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status != TaskStatus.COMPLETED) {
            this.completedAt = null;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
