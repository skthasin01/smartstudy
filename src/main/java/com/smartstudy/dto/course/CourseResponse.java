package com.smartstudy.dto.course;

import com.smartstudy.entity.Course;

import java.time.LocalDateTime;

public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private String colorCode;
    private String description;
    private LocalDateTime createdAt;

    public CourseResponse() {
    }

    public CourseResponse(Long id, String courseCode, String courseName, String colorCode,
                          String description, LocalDateTime createdAt) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.colorCode = colorCode;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static CourseResponse fromEntity(Course course) {
        if (course == null) return null;
        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getColorCode(),
                course.getDescription(),
                course.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
