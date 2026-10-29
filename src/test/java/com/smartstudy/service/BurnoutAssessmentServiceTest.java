package com.smartstudy.service;

import com.smartstudy.dto.burnout.BurnoutAssessmentResponse;
import com.smartstudy.dto.workload.WorkloadResponse;
import com.smartstudy.entity.BurnoutAssessment;
import com.smartstudy.entity.DailyCheckin;
import com.smartstudy.entity.User;
import com.smartstudy.entity.UserPrivacySettings;
import com.smartstudy.entity.enums.BurnoutRiskLevel;
import com.smartstudy.entity.enums.MoodLevel;
import com.smartstudy.entity.enums.RiskCategory;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.repository.BurnoutAssessmentRepository;
import com.smartstudy.repository.DailyCheckinRepository;
import com.smartstudy.repository.StudySessionRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserPrivacySettingsRepository;
import com.smartstudy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BurnoutAssessmentServiceTest {

    @Mock
    private BurnoutAssessmentRepository burnoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyTaskRepository taskRepository;

    @Mock
    private StudySessionRepository sessionRepository;

    @Mock
    private DailyCheckinRepository checkinRepository;

    @Mock
    private UserPrivacySettingsRepository privacySettingsRepository;

    @Mock
    private WorkloadAnalysisService workloadService;

    @Mock
    private RecommendationService recommendationService;

    private BurnoutAssessmentService burnoutService;
    private User student;

    @BeforeEach
    void setUp() {
        burnoutService = new BurnoutAssessmentService(
                burnoutRepository,
                userRepository,
                taskRepository,
                sessionRepository,
                checkinRepository,
                privacySettingsRepository,
                workloadService,
                recommendationService
        );

        student = new User("student@example.com", "pass", "Student", Role.ROLE_STUDENT);
        student.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(privacySettingsRepository.findByUserId(1L)).thenReturn(Optional.of(new UserPrivacySettings(student)));
        when(burnoutRepository.findByUserIdAndAssessmentDate(any(), any())).thenReturn(Optional.empty());
        when(burnoutRepository.save(any(BurnoutAssessment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Assessment without wellness data calculates risk purely on academic workload without errors")
    void assessBurnout_WithoutWellnessData_CalculatesCorrectly() {
        WorkloadResponse mockWorkload = new WorkloadResponse();
        mockWorkload.setWorkloadScore(20.0);
        mockWorkload.setRiskCategory(RiskCategory.LOW);

        when(workloadService.getCurrentWorkload(1L)).thenReturn(mockWorkload);
        when(taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(any(), any(), any())).thenReturn(Collections.emptyList());
        when(sessionRepository.findByUserIdAndStartTimeAfter(any(), any())).thenReturn(Collections.emptyList());
        when(checkinRepository.findByUserIdAndCheckinDateAfterOrderByCheckinDateAsc(any(), any())).thenReturn(Collections.emptyList());

        BurnoutAssessmentResponse res = burnoutService.assessBurnout(1L);

        assertNotNull(res);
        assertEquals(BurnoutRiskLevel.LOW, res.getRiskLevel());
        assertTrue(res.getRiskScore() < 40.0);
        assertTrue(res.getExplanation().contains("not a medical or psychiatric diagnosis"));
        verify(recommendationService).generateRecommendationsForAssessment(any(BurnoutAssessment.class));
    }

    @Test
    @DisplayName("High workload combined with high stress checkin triggers elevated burnout risk and recommendations")
    void assessBurnout_HighWorkloadAndStress_ProducesHighRisk() {
        WorkloadResponse highWorkload = new WorkloadResponse();
        highWorkload.setWorkloadScore(85.0);
        highWorkload.setRiskCategory(RiskCategory.HIGH);

        when(workloadService.getCurrentWorkload(1L)).thenReturn(highWorkload);
        when(taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(any(), any(), any())).thenReturn(Collections.emptyList());
        when(sessionRepository.findByUserIdAndStartTimeAfter(any(), any())).thenReturn(Collections.emptyList());

        DailyCheckin stressedCheckin = new DailyCheckin(
                student, LocalDate.now(), 5, 1, MoodLevel.VERY_LOW, 4.0, "Stressed"
        );
        when(checkinRepository.findByUserIdAndCheckinDateAfterOrderByCheckinDateAsc(any(), any()))
                .thenReturn(Collections.singletonList(stressedCheckin));

        BurnoutAssessmentResponse res = burnoutService.assessBurnout(1L);

        assertNotNull(res);
        assertTrue(res.getRiskLevel() == BurnoutRiskLevel.MODERATE || res.getRiskLevel() == BurnoutRiskLevel.HIGH);
        assertTrue(res.getContributingFactors().contains("High self-reported stress"));
        assertTrue(res.getContributingFactors().contains("Low sleep duration"));
        verify(recommendationService).generateRecommendationsForAssessment(any(BurnoutAssessment.class));
    }
}
