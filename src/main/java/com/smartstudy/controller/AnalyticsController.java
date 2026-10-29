package com.smartstudy.controller;

import com.smartstudy.dto.analytics.DashboardAnalyticsResponse;
import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics & Dashboard", description = "Aggregated student productivity, study hours, workload, and burnout indicators")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get full aggregated dashboard analytics for the current student")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        DashboardAnalyticsResponse dashboard = analyticsService.getDashboardData(userId);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
