package com.smartstudy.controller;

import com.smartstudy.dto.auth.ChangePasswordRequest;
import com.smartstudy.dto.auth.UpdateProfileRequest;
import com.smartstudy.dto.auth.UserProfileResponse;
import com.smartstudy.dto.common.ApiResponse;
import com.smartstudy.security.SecurityUtils;
import com.smartstudy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing the current student profile and credentials")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current student profile", description = "Retrieves information and privacy defaults for the authenticated user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update student profile", description = "Updates full name and basic profile fields.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserProfileResponse updated = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Verifies current password and updates to a new BCrypt hashed password.")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }
}
