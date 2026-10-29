package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.workload.WorkloadResponse;
import com.smartstudy.dto.workload.WorkloadTrendItem;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.WorkloadAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workload")
@Tag(name = "Workload Analysis", description = "Workload index calculation, factor breakdowns, and historical trends")
public class WorkloadController {

    private final WorkloadAnalysisService workloadService;

    public WorkloadController(WorkloadAnalysisService workloadService) {
        this.workloadService = workloadService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get current workload analysis and risk category")
    public ResponseEntity<ApiResponse<WorkloadResponse>> getCurrentWorkload() {
        Long userId = SecurityUtils.getCurrentUserId();
        WorkloadResponse workload = workloadService.getCurrentWorkload(userId);
        return ResponseEntity.ok(ApiResponse.success(workload));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Trigger on-demand workload index re-calculation")
    public ResponseEntity<ApiResponse<WorkloadResponse>> calculateWorkload() {
        Long userId = SecurityUtils.getCurrentUserId();
        WorkloadResponse workload = workloadService.calculateWorkload(userId);
        return ResponseEntity.ok(ApiResponse.success("Workload re-evaluated successfully", workload));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get historical workload score trends for charting")
    public ResponseEntity<ApiResponse<List<WorkloadTrendItem>>> getWorkloadTrends(@RequestParam(defaultValue = "14") int days) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<WorkloadTrendItem> trends = workloadService.getWorkloadTrends(userId, days);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
