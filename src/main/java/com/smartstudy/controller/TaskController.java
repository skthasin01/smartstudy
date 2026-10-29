package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.task.TaskFilterRequest;
import com.smartstudy.dto.task.TaskRequest;
import com.smartstudy.dto.task.TaskResponse;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Complete CRUD, search, filter, and pagination endpoints for study tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Search and filter tasks with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> getTasks(
            TaskFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Long userId = SecurityUtils.getCurrentUserId();
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PagedResponse<TaskResponse> tasks = taskService.getTasks(userId, filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task details by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskResponse task = taskService.getTask(userId, id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @PostMapping
    @Operation(summary = "Create a new study task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskResponse created = taskService.createTask(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Task created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskResponse updated = taskService.updateTask(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (e.g. mark COMPLETED or IN_PROGRESS)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskResponse updated = taskService.updateTaskStatus(userId, id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        taskService.deleteTask(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
