package com.smartstudy.service;

import com.smartstudy.dto.plan.StudyPlanRequest;
import com.smartstudy.dto.plan.StudyPlanResponse;
import com.smartstudy.entity.Course;
import com.smartstudy.entity.StudyPlan;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;
import com.smartstudy.repository.StudyPlanItemRepository;
import com.smartstudy.repository.StudyPlanRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudyPlanServiceTest {

    @Mock
    private StudyPlanRepository studyPlanRepository;

    @Mock
    private StudyPlanItemRepository planItemRepository;

    @Mock
    private StudyTaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private StudyPlanService studyPlanService;
    private User student;

    @BeforeEach
    void setUp() {
        studyPlanService = new StudyPlanService(studyPlanRepository, planItemRepository, taskRepository, userRepository);

        student = new User("student@example.com", "pass", "Student", Role.ROLE_STUDENT);
        student.setId(1L);
    }

    @Test
    @DisplayName("Generate plan divides long tasks into blocks and stays within daily cap")
    void generatePlan_DividesIntoBlocksAndRespectsCap() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(3);

        StudyTask urgentTask = new StudyTask(
                student, null, "Urgent Project", "Long project",
                TaskType.PROJECT, TaskDifficulty.HARD, 150, // 150 mins -> 3 blocks of 50m
                today.plusDays(2).atTime(23, 59), TaskPriority.URGENT
        );
        urgentTask.setId(1L);

        StudyTask minorTask = new StudyTask(
                student, null, "Quick Quiz Prep", "Prep quiz",
                TaskType.PREPARATION, TaskDifficulty.EASY, 50,
                today.plusDays(3).atTime(23, 59), TaskPriority.LOW
        );
        minorTask.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(taskRepository.findByUserIdAndStatusNot(1L, TaskStatus.COMPLETED))
                .thenReturn(Arrays.asList(urgentTask, minorTask));
        when(studyPlanRepository.findByUserIdAndIsCurrent(1L, true))
                .thenReturn(Collections.emptyList());
        when(studyPlanRepository.save(any(StudyPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Daily available 100 minutes, max block 50 minutes, break 10 minutes
        StudyPlanRequest req = new StudyPlanRequest(startDate, endDate, 100, 50, 10);
        StudyPlanResponse response = studyPlanService.generatePlan(1L, req);

        assertNotNull(response);
        assertEquals(200, response.getTotalAllocatedMinutes()); // 150 + 50 = 200 mins
        assertEquals(4, response.getItems().size()); // 3 blocks for urgentTask + 1 block for minorTask
        assertTrue(response.isCurrent());
        assertNotNull(response.getExplanation());
        assertTrue(response.getExplanation().contains("Total Study Blocks Scheduled: 4"));
    }

    @Test
    @DisplayName("Generate plan notes unscheduled tasks when planning time is insufficient")
    void generatePlan_InsufficientTime_ReportsUnscheduled() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        LocalDate endDate = today; // Only 1 day

        StudyTask hugeTask = new StudyTask(
                student, null, "Massive Research Paper", "Write thesis",
                TaskType.PROJECT, TaskDifficulty.HARD, 300,
                today.atTime(23, 59), TaskPriority.URGENT
        );
        hugeTask.setId(5L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(taskRepository.findByUserIdAndStatusNot(1L, TaskStatus.COMPLETED))
                .thenReturn(Collections.singletonList(hugeTask));
        when(studyPlanRepository.findByUserIdAndIsCurrent(1L, true))
                .thenReturn(Collections.emptyList());
        when(studyPlanRepository.save(any(StudyPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Daily cap is only 60 minutes, but task needs 300 minutes on that 1 day
        StudyPlanRequest req = new StudyPlanRequest(startDate, endDate, 60, 50, 10);
        StudyPlanResponse response = studyPlanService.generatePlan(1L, req);

        assertNotNull(response);
        // Should allocate only up to the 60 min cap
        assertTrue(response.getTotalAllocatedMinutes() <= 60);
        assertTrue(response.getExplanation().contains("WARNING - Unscheduled Workload Detected"));
    }
}
