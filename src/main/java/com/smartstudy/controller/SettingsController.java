package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.settings.PrivacySettingsRequest;
import com.smartstudy.dto.settings.PrivacySettingsResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.PrivacySettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings & Privacy", description = "Privacy preferences, study duration goals, and wellness data erasure controls")
public class SettingsController {

    private final PrivacySettingsService settingsService;

    public SettingsController(PrivacySettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    @Operation(summary = "Get student privacy settings and study goals")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> getSettings() {
        Long userId = SecurityUtils.getCurrentUserId();
        PrivacySettingsResponse settings = settingsService.getSettings(userId);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping
    @Operation(summary = "Update privacy preferences and daily study goal")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> updateSettings(@Valid @RequestBody PrivacySettingsRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PrivacySettingsResponse updated = settingsService.updateSettings(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Settings updated successfully", updated));
    }

    @DeleteMapping("/wellness-data")
    @Operation(summary = "Clear all logged wellness data (GDPR / Privacy protection)")
    public ResponseEntity<ApiResponse<Void>> clearWellnessData() {
        Long userId = SecurityUtils.getCurrentUserId();
        settingsService.clearWellnessData(userId);
        return ResponseEntity.ok(ApiResponse.success("All wellness records permanently deleted", null));
    }
}
