package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.plan.StudyPlanItemResponse;
import com.smartstudy.dto.plan.StudyPlanRequest;
import com.smartstudy.dto.plan.StudyPlanResponse;
import com.smartstudy.dto.plan.UpdatePlanItemStatusRequest;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/study-plans")
@Tag(name = "Study Plans", description = "Rule-based automatic study plan generation and scheduled item tracking")
public class StudyPlanController {

    private final StudyPlanService planService;

    public StudyPlanController(StudyPlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate automatic rule-based study plan", description = "Allocates study blocks across available days based on task deadlines, difficulty, and student daily study caps.")
    public ResponseEntity<ApiResponse<StudyPlanResponse>> generatePlan(@Valid @RequestBody StudyPlanRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        StudyPlanResponse response = planService.generatePlan(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Study plan generated successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/current")
    @Operation(summary = "Get currently active study plan and schedule")
    public ResponseEntity<ApiResponse<StudyPlanResponse>> getCurrentPlan() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudyPlanResponse response = planService.getCurrentPlan(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get historical generated study plans")
    public ResponseEntity<ApiResponse<PagedResponse<StudyPlanResponse>>> getPlanHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<StudyPlanResponse> history = planService.getPlanHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PatchMapping("/items/{itemId}/status")
    @Operation(summary = "Update status of an individual scheduled study block")
    public ResponseEntity<ApiResponse<StudyPlanItemResponse>> updateItemStatus(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdatePlanItemStatusRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        StudyPlanItemResponse response = planService.updatePlanItemStatus(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Plan item updated", response));
    }
}
