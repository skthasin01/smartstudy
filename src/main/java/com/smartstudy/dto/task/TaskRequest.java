package com.smartstudy.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(min = 2, max = 200, message = "Task title must be between 2 and 200 characters")
    private String title;

    @Size(max = 1500, message = "Description cannot exceed 1500 characters")
    private String description;

    private Long courseId;

    private TaskType taskType = TaskType.ASSIGNMENT;

    private TaskDifficulty difficulty = TaskDifficulty.MEDIUM;

    @NotNull(message = "Estimated minutes is required")
    @Min(value = 5, message = "Estimated minutes must be at least 5 minutes")
    @Max(value = 1440, message = "Estimated minutes cannot exceed 1440 minutes (24 hours)")
    private Integer estimatedMinutes;

    @NotNull(message = "Deadline is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private TaskStatus status = TaskStatus.TODO;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, Long courseId, TaskType taskType,
                       TaskDifficulty difficulty, Integer estimatedMinutes,
                       LocalDateTime deadline, TaskPriority priority, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.courseId = courseId;
        this.taskType = taskType != null ? taskType : TaskType.ASSIGNMENT;
        this.difficulty = difficulty != null ? difficulty : TaskDifficulty.MEDIUM;
        this.estimatedMinutes = estimatedMinutes;
        this.deadline = deadline;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.status = status != null ? status : TaskStatus.TODO;
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
}
