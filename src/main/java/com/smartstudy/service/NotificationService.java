package com.smartstudy.service;

import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.notification.NotificationResponse;
import com.smartstudy.entity.Notification;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.NotificationType;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.NotificationRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type) {
        // Prevent duplicate spam of the same notification type within the past 12 hours
        LocalDateTime recentThreshold = LocalDateTime.now().minusHours(12);
        if (notificationRepository.existsByUserIdAndTypeAndCreatedAtAfter(user.getId(), type, recentThreshold)) {
            return;
        }

        Notification notification = new Notification(user, title, message, type);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<NotificationResponse> responses = new ArrayList<>();
        for (Notification n : page.getContent()) {
            responses.add(NotificationResponse.fromEntity(n));
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

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notificationRepository.delete(notification);
    }
}
