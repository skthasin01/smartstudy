package com.smartstudy.controller;

import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.session.EndSessionRequest;
import com.smartstudy.dto.session.SessionResponse;
import com.smartstudy.dto.session.StartSessionRequest;
import com.smartstudy.dto.session.StudySummaryResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.StudySessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/study-sessions")
@Tag(name = "Study Sessions", description = "Real-time session tracker, Pomodoro break logging, and study duration summaries")
public class StudySessionController {

    private final StudySessionService sessionService;

    public StudySessionController(StudySessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start a new live study session", description = "Records start time on server; prevents concurrent active sessions.")
    public ResponseEntity<ApiResponse<SessionResponse>> startSession(@RequestBody(required = false) StartSessionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse response = sessionService.startSession(userId, request);
        return new ResponseEntity<>(ApiResponse.success("Study session started", response), HttpStatus.CREATED);
    }

    @PostMapping("/end")
    @Operation(summary = "Conclude the active study session", description = "Computes elapsed time on server, logs breaks, and updates linked task progress.")
    public ResponseEntity<ApiResponse<SessionResponse>> endSession(@Valid @RequestBody(required = false) EndSessionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse response = sessionService.endSession(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Study session completed", response));
    }

    @GetMapping("/active")
    @Operation(summary = "Get currently active study session if any")
    public ResponseEntity<ApiResponse<SessionResponse>> getActiveSession() {
        Long userId = SecurityUtils.getCurrentUserId();
        SessionResponse response = sessionService.getActiveSession(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get historical study sessions with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<SessionResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<SessionResponse> history = sessionService.getSessionHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get daily and weekly study totals")
    public ResponseEntity<ApiResponse<StudySummaryResponse>> getSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        StudySummaryResponse summary = sessionService.getSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
