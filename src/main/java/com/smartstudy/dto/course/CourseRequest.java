package com.smartstudy.dto.course;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CourseRequest {

    @NotBlank(message = "Course code is required")
    @Size(min = 2, max = 30, message = "Course code must be between 2 and 30 characters")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters")
    private String courseName;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid hexadecimal color code (e.g. #4F46E5)")
    private String colorCode = "#4F46E5";

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public CourseRequest() {
    }

    public CourseRequest(String courseCode, String courseName, String colorCode, String description) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.colorCode = colorCode != null ? colorCode : "#4F46E5";
        this.description = description;
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
}
