package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.course.CourseRequest;
import com.smartstudy.dto.course.CourseResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management endpoints for student subjects and color tagging")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Get all courses for current student")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CourseResponse> courses = courseService.getCourses(userId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        CourseResponse course = courseService.getCourse(userId, id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CourseResponse course = courseService.createCourse(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Course created successfully", course), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing course")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CourseResponse course = courseService.updateCourse(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", course));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        courseService.deleteCourse(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }
}
