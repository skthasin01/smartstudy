package com.smartstudy.service;

import com.smartstudy.dto.session.EndSessionRequest;
import com.smartstudy.dto.session.SessionResponse;
import com.smartstudy.dto.session.StartSessionRequest;
import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.entity.enums.SessionStatus;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.repository.StudySessionRepository;
import com.smartstudy.repository.StudyTaskRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StudySessionServiceTest {

    @Mock
    private StudySessionRepository sessionRepository;

    @Mock
    private StudyTaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private StudySessionService sessionService;
    private User student;
    private StudyTask task;

    @BeforeEach
    void setUp() {
        sessionService = new StudySessionService(sessionRepository, taskRepository, userRepository);

        student = new User("student@example.com", "pass", "Student", Role.ROLE_STUDENT);
        student.setId(1L);

        task = new StudyTask(student, null, "Homework", "Math",
                TaskType.ASSIGNMENT, TaskDifficulty.MEDIUM, 60,
                LocalDateTime.now().plusDays(2), TaskPriority.MEDIUM);
        task.setId(10L);
    }

    @Test
    @DisplayName("Start session records server-side start time and sets status ACTIVE")
    void startSession_Success() {
        StartSessionRequest req = new StartSessionRequest(10L, "Focusing on chapter 2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sessionRepository.findByUserIdAndStatus(1L, SessionStatus.ACTIVE)).thenReturn(Optional.empty());
        when(taskRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(task));

        StudySession savedSession = new StudySession(student, task, LocalDateTime.now(), "Focusing on chapter 2");
        savedSession.setId(55L);
        when(sessionRepository.save(any(StudySession.class))).thenReturn(savedSession);

        SessionResponse res = sessionService.startSession(1L, req);

        assertNotNull(res);
        assertEquals(55L, res.getId());
        assertEquals(SessionStatus.ACTIVE, res.getStatus());
        assertEquals(10L, res.getTaskId());
        assertNotNull(res.getStartTime());
    }

    @Test
    @DisplayName("Starting session when one is already active throws BadRequestException")
    void startSession_DuplicateActive_ThrowsBadRequest() {
        StudySession existingActive = new StudySession(student, null, LocalDateTime.now().minusMinutes(20), "Active");

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sessionRepository.findByUserIdAndStatus(1L, SessionStatus.ACTIVE)).thenReturn(Optional.of(existingActive));

        assertThrows(BadRequestException.class, () -> sessionService.startSession(1L, new StartSessionRequest()));
    }

    @Test
    @DisplayName("End session reliably computes duration and completes task if requested")
    void endSession_ComputesDurationAndCompletesTask() {
        StudySession active = new StudySession(student, task, LocalDateTime.now().minusMinutes(45), "Studying");
        active.setId(55L);

        when(sessionRepository.findByUserIdAndStatus(1L, SessionStatus.ACTIVE)).thenReturn(Optional.of(active));
        when(sessionRepository.save(any(StudySession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EndSessionRequest req = new EndSessionRequest(5, "Wrapped up", true);
        SessionResponse res = sessionService.endSession(1L, req);

        assertNotNull(res);
        assertEquals(SessionStatus.COMPLETED, res.getStatus());
        assertTrue(res.getActualDurationMinutes() >= 44 && res.getActualDurationMinutes() <= 46);
        assertEquals(5, res.getBreakDurationMinutes());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        verify(taskRepository).save(task);
    }
}
