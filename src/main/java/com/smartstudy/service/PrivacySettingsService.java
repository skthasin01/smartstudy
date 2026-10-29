package com.smartstudy.service;

import com.smartstudy.dto.settings.PrivacySettingsRequest;
import com.smartstudy.dto.settings.PrivacySettingsResponse;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.DailyCheckinRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrivacySettingsService {

    private final UserPrivacySettingsRepository privacySettingsRepository;
    private final UserRepository userRepository;
    private final DailyCheckinRepository checkinRepository;

    public PrivacySettingsService(UserPrivacySettingsRepository privacySettingsRepository,
                                  UserRepository userRepository,
                                  DailyCheckinRepository checkinRepository) {
        this.privacySettingsRepository = privacySettingsRepository;
        this.userRepository = userRepository;
        this.checkinRepository = checkinRepository;
    }

    @Transactional(readOnly = true)
    public PrivacySettingsResponse getSettings(Long userId) {
        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    UserPrivacySettings newSettings = new UserPrivacySettings(user);
                    return privacySettingsRepository.save(newSettings);
                });

        return PrivacySettingsResponse.fromEntity(settings);
    }

    @Transactional
    public PrivacySettingsResponse updateSettings(Long userId, PrivacySettingsRequest request) {
        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    return new UserPrivacySettings(user);
                });

        settings.setAllowWellnessTracking(request.isAllowWellnessTracking());
        settings.setShareAnonymousStats(request.isShareAnonymousStats());
        settings.setReminderNotificationsEnabled(request.isReminderNotificationsEnabled());
        if (request.getDailyStudyGoalMinutes() != null) {
            settings.setDailyStudyGoalMinutes(request.getDailyStudyGoalMinutes());
        }

        UserPrivacySettings saved = privacySettingsRepository.save(settings);
        return PrivacySettingsResponse.fromEntity(saved);
    }

    @Transactional
    public void clearWellnessData(Long userId) {
        checkinRepository.deleteByUserId(userId);
    }
}
