package com.smartstudy.service;

import com.smartstudy.dto.auth.ChangePasswordRequest;
import com.smartstudy.dto.auth.UpdateProfileRequest;
import com.smartstudy.dto.auth.UserProfileResponse;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserPrivacySettingsRepository privacySettingsRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.privacySettingsRepository = privacySettingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId)
                .orElse(null);

        boolean allowWellness = settings != null && settings.isAllowWellnessTracking();
        Integer dailyGoal = settings != null ? settings.getDailyStudyGoalMinutes() : 240;

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt(),
                allowWellness,
                dailyGoal
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFullName(request.getFullName().trim());
        userRepository.save(user);

        return getProfile(userId);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BadRequestException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
