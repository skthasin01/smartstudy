package com.smartstudy.dto.plan;

import com.smartstudy.entity.StudyPlanItem;
import com.smartstudy.entity.enums.PlanItemStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudyPlanItemResponse {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private String courseCode;
    private String courseColor;
    private String title;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private PlanItemStatus status;
    private String notes;

    public StudyPlanItemResponse() {
    }

    public static StudyPlanItemResponse fromEntity(StudyPlanItem item) {
        if (item == null) return null;

        StudyPlanItemResponse res = new StudyPlanItemResponse();
        res.setId(item.getId());
        if (item.getTask() != null) {
            res.setTaskId(item.getTask().getId());
            res.setTaskTitle(item.getTask().getTitle());
            if (item.getTask().getCourse() != null) {
                res.setCourseCode(item.getTask().getCourse().getCourseCode());
                res.setCourseColor(item.getTask().getCourse().getColorCode());
            }
        }
        res.setTitle(item.getTitle());
        res.setScheduledDate(item.getScheduledDate());
        res.setStartTime(item.getStartTime());
        res.setEndTime(item.getEndTime());
        res.setDurationMinutes(item.getDurationMinutes());
        res.setStatus(item.getStatus());
        res.setNotes(item.getNotes());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseColor() {
        return courseColor;
    }

    public void setCourseColor(String courseColor) {
        this.courseColor = courseColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public PlanItemStatus getStatus() {
        return status;
    }

    public void setStatus(PlanItemStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
