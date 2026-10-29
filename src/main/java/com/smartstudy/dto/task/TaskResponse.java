package com.smartstudy.dto.task;

import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String colorCode;
    private TaskType taskType;
    private TaskDifficulty difficulty;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private LocalDateTime deadline;
    private TaskPriority priority;
    private TaskStatus status;
    private boolean overdue;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public TaskResponse() {
    }

    public static TaskResponse fromEntity(StudyTask task) {
        if (task == null) return null;

        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        if (task.getCourse() != null) {
            res.setCourseId(task.getCourse().getId());
            res.setCourseCode(task.getCourse().getCourseCode());
            res.setCourseName(task.getCourse().getCourseName());
            res.setColorCode(task.getCourse().getColorCode());
        }
        res.setTaskType(task.getTaskType());
        res.setDifficulty(task.getDifficulty());
        res.setEstimatedMinutes(task.getEstimatedMinutes());
        res.setActualMinutes(task.getActualMinutes() != null ? task.getActualMinutes() : 0);
        res.setDeadline(task.getDeadline());
        res.setPriority(task.getPriority());
        res.setStatus(task.getStatus());
        res.setOverdue(task.getStatus() != TaskStatus.COMPLETED &&
                task.getDeadline() != null &&
                task.getDeadline().isBefore(LocalDateTime.now()));
        res.setCreatedAt(task.getCreatedAt());
        res.setCompletedAt(task.getCompletedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
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
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
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
