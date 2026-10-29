package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.recommendation.RecommendationResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Personalized and supportive study pacing and wellness recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    @Operation(summary = "Get active recommendations for current student")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<RecommendationResponse> list = recommendationService.getActiveRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a recommendation as read")
    public ResponseEntity<ApiResponse<RecommendationResponse>> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        RecommendationResponse res = recommendationService.markAsRead(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", res));
    }

    @PatchMapping("/{id}/dismiss")
    @Operation(summary = "Dismiss a recommendation")
    public ResponseEntity<ApiResponse<Void>> dismiss(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        recommendationService.dismiss(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Recommendation dismissed", null));
    }
}
