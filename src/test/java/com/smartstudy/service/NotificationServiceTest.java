package com.smartstudy.service;

import com.smartstudy.dto.notification.NotificationResponse;
import com.smartstudy.entity.Notification;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.NotificationType;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.repository.NotificationRepository;
import com.smartstudy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationService notificationService;
    private User student;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, userRepository);

        student = new User("student@example.com", "pass", "Student", Role.ROLE_STUDENT);
        student.setId(1L);
    }

    @Test
    @DisplayName("Create notification should create record if no duplicate sent recently")
    void createNotification_New_SavesRecord() {
        when(notificationRepository.existsByUserIdAndTypeAndCreatedAtAfter(eq(1L), eq(NotificationType.DEADLINE_APPROACHING), any(LocalDateTime.class)))
                .thenReturn(false);

        notificationService.createNotification(student, "Deadline Soon", "Submit by 5pm", NotificationType.DEADLINE_APPROACHING);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Create notification should suppress duplicate alert if sent recently")
    void createNotification_Duplicate_SuppressesNotification() {
        when(notificationRepository.existsByUserIdAndTypeAndCreatedAtAfter(eq(1L), eq(NotificationType.DEADLINE_APPROACHING), any(LocalDateTime.class)))
                .thenReturn(true);

        notificationService.createNotification(student, "Deadline Soon", "Submit by 5pm", NotificationType.DEADLINE_APPROACHING);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Mark as read updates notification status")
    void markAsRead_Success() {
        Notification notification = new Notification(student, "Alert", "Body", NotificationType.BREAK_REMINDER);
        notification.setId(7L);
        assertFalse(notification.isRead());

        when(notificationRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse res = notificationService.markAsRead(1L, 7L);

        assertNotNull(res);
        assertTrue(res.isRead());
    }

    @Test
    @DisplayName("Mark all as read invokes batch repository update")
    void markAllAsRead_Success() {
        notificationService.markAllAsRead(1L);
        verify(notificationRepository).markAllAsReadForUser(1L);
    }
}
