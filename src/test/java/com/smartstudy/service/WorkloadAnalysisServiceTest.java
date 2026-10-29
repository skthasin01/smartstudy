package com.smartstudy.service;

import com.smartstudy.dto.workload.WorkloadResponse;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.WorkloadAnalysis;
import com.smartstudy.entity.enums.RiskCategory;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import com.smartstudy.repository.WorkloadAnalysisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkloadAnalysisServiceTest {

    @Mock
    private WorkloadAnalysisRepository workloadRepository;

    @Mock
    private StudyTaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivacySettingsRepository privacySettingsRepository;

    private WorkloadAnalysisService workloadService;
    private User student;

    @BeforeEach
    void setUp() {
        workloadService = new WorkloadAnalysisService(workloadRepository, taskRepository, userRepository, privacySettingsRepository);

        student = new User("student@example.com", "pass", "Student", Role.ROLE_STUDENT);
        student.setId(1L);

        UserPrivacySettings settings = new UserPrivacySettings(student);
        settings.setDailyStudyGoalMinutes(240); // 240 * 7 = 1680 mins capacity
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(privacySettingsRepository.findByUserId(1L)).thenReturn(Optional.of(settings));
        when(workloadRepository.findByUserIdAndAnalysisDate(any(), any())).thenReturn(Optional.empty());
        when(workloadRepository.save(any(WorkloadAnalysis.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Empty tasks should produce LOW risk workload")
    void calculateWorkload_EmptyTasks_ProducesLowWorkload() {
        when(taskRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        WorkloadResponse res = workloadService.calculateWorkload(1L);

        assertNotNull(res);
        assertEquals(0, res.getTotalTasks());
        assertEquals(0, res.getPendingTasks());
        assertEquals(0.0, res.getWorkloadScore());
        assertEquals(RiskCategory.LOW, res.getRiskCategory());
    }

    @Test
    @DisplayName("Heavy pending tasks with overdue deadline should calculate elevated risk and penalty")
    void calculateWorkload_HeavyTasksWithOverdue_CalculatesHighOrSevere() {
        LocalDateTime now = LocalDateTime.now();

        // 1 Overdue task
        StudyTask overdueTask = new StudyTask(
                student, null, "Overdue Assignment", "Late",
                TaskType.ASSIGNMENT, TaskDifficulty.HARD, 200,
                now.minusDays(1), TaskPriority.URGENT
        );

        // 2 Due soon tasks
        StudyTask dueTomorrow = new StudyTask(
                student, null, "Exam Tomorrow", "Prep",
                TaskType.EXAM, TaskDifficulty.HARD, 300,
                now.plusHours(12), TaskPriority.URGENT
        );

        when(taskRepository.findByUserId(1L)).thenReturn(Arrays.asList(overdueTask, dueTomorrow));

        WorkloadResponse res = workloadService.calculateWorkload(1L);

        assertNotNull(res);
        assertEquals(2, res.getTotalTasks());
        assertEquals(2, res.getPendingTasks());
        assertEquals(1, res.getMissedTasks());
        assertTrue(res.getWorkloadScore() > 35.0, "Score should be elevated due to hard tasks and overdue penalty");
        assertTrue(res.getRiskCategory() == RiskCategory.MODERATE || res.getRiskCategory() == RiskCategory.HIGH || res.getRiskCategory() == RiskCategory.SEVERE);
        assertTrue(res.getExplanation().contains("overdue task(s) adding strain"));
    }
}
