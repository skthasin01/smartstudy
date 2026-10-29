package com.smartstudy.service;

import com.smartstudy.dto.checkin.DailyCheckinRequest;
import com.smartstudy.dto.checkin.DailyCheckinResponse;
import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.entity.DailyCheckin;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.DailyCheckinRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DailyCheckinService {

    private final DailyCheckinRepository checkinRepository;
    private final UserRepository userRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;

    public DailyCheckinService(DailyCheckinRepository checkinRepository,
                               UserRepository userRepository,
                               UserPrivacySettingsRepository privacySettingsRepository) {
        this.checkinRepository = checkinRepository;
        this.userRepository = userRepository;
        this.privacySettingsRepository = privacySettingsRepository;
    }

    @Transactional
    public DailyCheckinResponse submitCheckin(Long userId, DailyCheckinRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId).orElse(null);
        if (settings != null && !settings.isAllowWellnessTracking()) {
            throw new BadRequestException("Wellness tracking is disabled in your privacy settings. Enable it in Settings to record check-ins.");
        }

        LocalDate date = request.getCheckinDate() != null ? request.getCheckinDate() : LocalDate.now();
        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the future");
        }

        Optional<DailyCheckin> existingOpt = checkinRepository.findByUserIdAndCheckinDate(userId, date);
        DailyCheckin checkin;

        if (existingOpt.isPresent()) {
            checkin = existingOpt.get();
            checkin.setStressLevel(request.getStressLevel());
            checkin.setEnergyLevel(request.getEnergyLevel());
            checkin.setMoodLevel(request.getMoodLevel());
            checkin.setSleepHours(request.getSleepHours());
            checkin.setNotes(request.getNotes());
        } else {
            checkin = new DailyCheckin(
                    user,
                    date,
                    request.getStressLevel(),
                    request.getEnergyLevel(),
                    request.getMoodLevel(),
                    request.getSleepHours(),
                    request.getNotes()
            );
        }

        DailyCheckin saved = checkinRepository.save(checkin);
        return DailyCheckinResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DailyCheckinResponse getTodayCheckin(Long userId) {
        DailyCheckin checkin = checkinRepository.findByUserIdAndCheckinDate(userId, LocalDate.now())
                .orElse(null);
        return DailyCheckinResponse.fromEntity(checkin);
    }

    @Transactional(readOnly = true)
    public PagedResponse<DailyCheckinResponse> getHistory(Long userId, Pageable pageable) {
        Page<DailyCheckin> page = checkinRepository.findByUserIdOrderByCheckinDateDesc(userId, pageable);
        List<DailyCheckinResponse> responses = new ArrayList<>();
        for (DailyCheckin c : page.getContent()) {
            responses.add(DailyCheckinResponse.fromEntity(c));
        }

        return new PagedResponse<>(
                responses,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional
    public void deleteCheckinData(Long userId) {
        checkinRepository.deleteByUserId(userId);
    }
}
