package com.smartstudy.controller;

import com.smartstudy.dto.checkin.DailyCheckinRequest;
import com.smartstudy.dto.checkin.DailyCheckinResponse;
import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.DailyCheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/check-ins")
@Tag(name = "Daily Wellness Check-ins", description = "Optional student wellness check-in endpoints (stress, energy, mood, sleep)")
public class DailyCheckinController {

    private final DailyCheckinService checkinService;

    public DailyCheckinController(DailyCheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's wellness check-in if submitted")
    public ResponseEntity<ApiResponse<DailyCheckinResponse>> getTodayCheckin() {
        Long userId = SecurityUtils.getCurrentUserId();
        DailyCheckinResponse checkin = checkinService.getTodayCheckin(userId);
        return ResponseEntity.ok(ApiResponse.success(checkin));
    }

    @PostMapping
    @Operation(summary = "Submit or update daily check-in")
    public ResponseEntity<ApiResponse<DailyCheckinResponse>> submitCheckin(@Valid @RequestBody DailyCheckinRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        DailyCheckinResponse checkin = checkinService.submitCheckin(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Daily check-in saved", checkin));
    }

    @GetMapping("/history")
    @Operation(summary = "Get historical check-in submissions with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<DailyCheckinResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "14") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<DailyCheckinResponse> history = checkinService.getHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @DeleteMapping("/my-data")
    @Operation(summary = "Delete all wellness data for the student (Privacy/GDPR control)")
    public ResponseEntity<ApiResponse<Void>> deleteMyData() {
        Long userId = SecurityUtils.getCurrentUserId();
        checkinService.deleteCheckinData(userId);
        return ResponseEntity.ok(ApiResponse.success("All wellness records removed successfully", null));
    }
}
