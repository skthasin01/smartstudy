package com.smartstudy.service;

import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.session.EndSessionRequest;
import com.smartstudy.dto.session.SessionResponse;
import com.smartstudy.dto.session.StartSessionRequest;
import com.smartstudy.dto.session.StudySummaryResponse;
import com.smartstudy.entity.StudySession;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.SessionStatus;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.StudySessionRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudySessionService {

    private final StudySessionRepository sessionRepository;
    private final StudyTaskRepository taskRepository;
    private final UserRepository userRepository;

    public StudySessionService(StudySessionRepository sessionRepository,
                               StudyTaskRepository taskRepository,
                               UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SessionResponse startSession(Long userId, StartSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent duplicate active sessions
        Optional<StudySession> activeOpt = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE);
        if (activeOpt.isPresent()) {
            throw new BadRequestException("You already have an active study session in progress. Please end it before starting a new one.");
        }

        StudyTask task = null;
        if (request != null && request.getTaskId() != null) {
            task = taskRepository.findByIdAndUserId(request.getTaskId(), userId)
                    .orElseThrow(() -> new BadRequestException("Selected task does not belong to you"));

            if (task.getStatus() == TaskStatus.TODO) {
                task.setStatus(TaskStatus.IN_PROGRESS);
                taskRepository.save(task);
            }
        }

        StudySession session = new StudySession(
                user,
                task,
                LocalDateTime.now(),
                request != null ? request.getNotes() : null
        );

        StudySession saved = sessionRepository.save(session);
        return SessionResponse.fromEntity(saved);
    }

    @Transactional
    public SessionResponse endSession(Long userId, EndSessionRequest request) {
        StudySession session = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException("No active study session found to conclude"));

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);

        // Server-side calculation of elapsed minutes for reliability
        long elapsedMinutes = ChronoUnit.MINUTES.between(session.getStartTime(), endTime);
        if (elapsedMinutes < 1) {
            elapsedMinutes = 1; // Minimum recorded time if ended within the minute
        }

        session.setActualDurationMinutes((int) elapsedMinutes);
        int breaks = (request != null && request.getBreakDurationMinutes() != null) ? request.getBreakDurationMinutes() : 0;
        session.setBreakDurationMinutes(breaks);
        session.setStatus(SessionStatus.COMPLETED);

        if (request != null && request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }

        // Update task progress if session was linked to a task
        if (session.getTask() != null) {
            StudyTask task = session.getTask();
            int currentActual = task.getActualMinutes() != null ? task.getActualMinutes() : 0;
            task.setActualMinutes(currentActual + (int) elapsedMinutes);

            if (request != null && request.isMarkTaskCompleted()) {
                task.setStatus(TaskStatus.COMPLETED);
            }
            taskRepository.save(task);
        }

        StudySession updated = sessionRepository.save(session);
        return SessionResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public SessionResponse getActiveSession(Long userId) {
        StudySession session = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE)
                .orElse(null);
        return SessionResponse.fromEntity(session);
    }

    @Transactional(readOnly = true)
    public PagedResponse<SessionResponse> getSessionHistory(Long userId, Pageable pageable) {
        Page<StudySession> page = sessionRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
        List<SessionResponse> responses = new ArrayList<>();
        for (StudySession s : page.getContent()) {
            responses.add(SessionResponse.fromEntity(s));
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
    public StudySummaryResponse getSummary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(23, 59, 59);

        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime startOfWeek = monday.atStartOfDay();

        List<StudySession> todaySessions = sessionRepository.findByUserIdAndStartTimeBetween(userId, startOfToday, endOfToday);
        List<StudySession> weekSessions = sessionRepository.findByUserIdAndStartTimeBetween(userId, startOfWeek, endOfToday);

        int todayMins = 0;
        for (StudySession s : todaySessions) {
            if (s.getStatus() == SessionStatus.COMPLETED && s.getActualDurationMinutes() != null) {
                todayMins += s.getActualDurationMinutes();
            }
        }

        int weekMins = 0;
        int completedCount = 0;
        int totalBreak = 0;
        for (StudySession s : weekSessions) {
            if (s.getStatus() == SessionStatus.COMPLETED) {
                completedCount++;
                if (s.getActualDurationMinutes() != null) {
                    weekMins += s.getActualDurationMinutes();
                }
                if (s.getBreakDurationMinutes() != null) {
                    totalBreak += s.getBreakDurationMinutes();
                }
            }
        }

        return new StudySummaryResponse(
                todayMins,
                weekMins,
                weekSessions.size(),
                completedCount,
                totalBreak
        );
    }
}
