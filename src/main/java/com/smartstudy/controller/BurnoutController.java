package com.smartstudy.controller;

import com.smartstudy.dto.burnout.BurnoutAssessmentResponse;
import com.smartstudy.dto.burnout.BurnoutTrendItem;
import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.BurnoutAssessmentService;
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
@RequestMapping("/api/burnout")
@Tag(name = "Burnout Risk Assessment", description = "Early-warning risk score, contributing strain indicators, and trend analytics")
public class BurnoutController {

    private final BurnoutAssessmentService burnoutService;

    public BurnoutController(BurnoutAssessmentService burnoutService) {
        this.burnoutService = burnoutService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get current burnout risk assessment and explanation")
    public ResponseEntity<ApiResponse<BurnoutAssessmentResponse>> getCurrentAssessment() {
        Long userId = SecurityUtils.getCurrentUserId();
        BurnoutAssessmentResponse response = burnoutService.getCurrentAssessment(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/assess")
    @Operation(summary = "Trigger on-demand burnout risk assessment and generate recommendations")
    public ResponseEntity<ApiResponse<BurnoutAssessmentResponse>> assessBurnout() {
        Long userId = SecurityUtils.getCurrentUserId();
        BurnoutAssessmentResponse response = burnoutService.assessBurnout(userId);
        return ResponseEntity.ok(ApiResponse.success("Burnout assessment completed", response));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get historical burnout risk trends for visualization")
    public ResponseEntity<ApiResponse<List<BurnoutTrendItem>>> getBurnoutTrends(@RequestParam(defaultValue = "14") int days) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<BurnoutTrendItem> trends = burnoutService.getBurnoutTrends(userId, days);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
