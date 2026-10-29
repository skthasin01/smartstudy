package com.smartstudy.dto.auth;

import com.smartstudy.entity.enums.Role;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private LocalDateTime createdAt;
    private boolean allowWellnessTracking;
    private Integer dailyStudyGoalMinutes;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String email, String fullName, Role role,
                               LocalDateTime createdAt, boolean allowWellnessTracking,
                               Integer dailyStudyGoalMinutes) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
        this.allowWellnessTracking = allowWellnessTracking;
        this.dailyStudyGoalMinutes = dailyStudyGoalMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAllowWellnessTracking() {
        return allowWellnessTracking;
    }

    public void setAllowWellnessTracking(boolean allowWellnessTracking) {
        this.allowWellnessTracking = allowWellnessTracking;
    }

    public Integer getDailyStudyGoalMinutes() {
        return dailyStudyGoalMinutes;
    }

    public void setDailyStudyGoalMinutes(Integer dailyStudyGoalMinutes) {
        this.dailyStudyGoalMinutes = dailyStudyGoalMinutes;
    }
}
