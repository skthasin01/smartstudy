package com.smartstudy.dto.session;

import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.enums.SessionStatus;

import java.time.LocalDateTime;

public class SessionResponse {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private String courseCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer actualDurationMinutes;
    private Integer breakDurationMinutes;
    private SessionStatus status;
    private String notes;

    public SessionResponse() {
    }

    public static SessionResponse fromEntity(StudySession session) {
        if (session == null) return null;

        SessionResponse res = new SessionResponse();
        res.setId(session.getId());
        if (session.getTask() != null) {
            res.setTaskId(session.getTask().getId());
            res.setTaskTitle(session.getTask().getTitle());
            if (session.getTask().getCourse() != null) {
                res.setCourseCode(session.getTask().getCourse().getCourseCode());
            }
        }
        res.setStartTime(session.getStartTime());
        res.setEndTime(session.getEndTime());
        res.setActualDurationMinutes(session.getActualDurationMinutes());
        res.setBreakDurationMinutes(session.getBreakDurationMinutes());
        res.setStatus(session.getStatus());
        res.setNotes(session.getNotes());
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
