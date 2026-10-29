package com.smartstudy.scheduler;

import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.enums.NotificationType;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import com.smartstudy.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Background Scheduler for automated reminders.
 * Runs hourly checks to notify students about approaching and missed deadlines.
 * Duplicate notifications within a 12-hour window are suppressed automatically by NotificationService.
 */
@Component
public class StudyReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StudyReminderScheduler.class);

    private final StudyTaskRepository taskRepository;
    private final NotificationService notificationService;
    private final UserPrivacySettingsRepository privacySettingsRepository;

    public StudyReminderScheduler(StudyTaskRepository taskRepository,
                                  NotificationService notificationService,
                                  UserPrivacySettingsRepository privacySettingsRepository) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
        this.privacySettingsRepository = privacySettingsRepository;
    }

    /**
     * Executes every hour to detect tasks with deadlines within the next 24 hours
     * or tasks that have recently passed their deadline without completion.
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 60000)
    @Transactional
    public void checkDeadlinesAndSendReminders() {
        logger.info("Executing scheduled study deadline check...");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        // 1. Check for upcoming deadlines (due in next 24 hours)
        List<StudyTask> upcoming = taskRepository.findByDeadlineBetweenAndStatusNot(now, next24Hours, TaskStatus.COMPLETED);
        for (StudyTask task : upcoming) {
            User user = task.getUser();
            if (isNotificationEnabled(user.getId())) {
                String title = "Deadline Approaching: " + task.getTitle();
                String message = String.format("Task '%s' is due soon on %s. Allocate study time to wrap it up.",
                        task.getTitle(), task.getDeadline().toString().replace('T', ' '));
                notificationService.createNotification(user, title, message, NotificationType.DEADLINE_APPROACHING);
            }
        }

        // 2. Check for overdue tasks
        List<StudyTask> overdue = taskRepository.findByDeadlineBeforeAndStatusNot(now, TaskStatus.COMPLETED);
        for (StudyTask task : overdue) {
            User user = task.getUser();
            if (isNotificationEnabled(user.getId())) {
                String title = "Missed Deadline: " + task.getTitle();
                String message = String.format("Task '%s' was due on %s. Review your study plan to reschedule or submit late if permitted.",
                        task.getTitle(), task.getDeadline().toString().replace('T', ' '));
                notificationService.createNotification(user, title, message, NotificationType.DEADLINE_MISSED);
            }
        }
    }

    private boolean isNotificationEnabled(Long userId) {
        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId).orElse(null);
        return settings == null || settings.isReminderNotificationsEnabled();
    }
}
