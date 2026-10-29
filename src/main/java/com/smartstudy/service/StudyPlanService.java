package com.smartstudy.service;

import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.plan.StudyPlanItemResponse;
import com.smartstudy.dto.plan.StudyPlanRequest;
import com.smartstudy.dto.plan.StudyPlanResponse;
import com.smartstudy.dto.plan.UpdatePlanItemStatusRequest;
import com.smartstudy.entity.StudyPlan;
import com.smartstudy.entity.StudyPlanItem;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.PlanItemStatus;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.StudyPlanItemRepository;
import com.smartstudy.repository.StudyPlanRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanItemRepository planItemRepository;
    private final StudyTaskRepository taskRepository;
    private final UserRepository userRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository,
                            StudyPlanItemRepository planItemRepository,
                            StudyTaskRepository taskRepository,
                            UserRepository userRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.planItemRepository = planItemRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public StudyPlanResponse generatePlan(Long userId, StudyPlanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        // Fetch pending tasks
        List<StudyTask> pendingTasks = taskRepository.findByUserIdAndStatusNot(userId, TaskStatus.COMPLETED);

        // Sort pending tasks by urgency: deadline proximity, then priority, then difficulty
        Collections.sort(pendingTasks, new Comparator<StudyTask>() {
            @Override
            public int compare(StudyTask t1, StudyTask t2) {
                // 1. Deadline comparison
                int deadlineComp = t1.getDeadline().compareTo(t2.getDeadline());
                if (deadlineComp != 0) {
                    return deadlineComp;
                }
                // 2. Priority comparison (URGENT > HIGH > MEDIUM > LOW)
                int p1 = getPriorityWeight(t1.getPriority());
                int p2 = getPriorityWeight(t2.getPriority());
                if (p1 != p2) {
                    return Integer.compare(p2, p1);
                }
                // 3. Difficulty comparison (HARD > MEDIUM > EASY)
                int d1 = getDifficultyWeight(t1.getDifficulty());
                int d2 = getDifficultyWeight(t2.getDifficulty());
                return Integer.compare(d2, d1);
            }
        });

        int dailyCap = request.getDailyAvailableMinutes() != null ? request.getDailyAvailableMinutes() : 240;
        int maxBlock = request.getMaxBlockMinutes() != null ? request.getMaxBlockMinutes() : 50;
        int breakBuffer = request.getBreakMinutes() != null ? request.getBreakMinutes() : 10;

        // Deactivate previous active plans for this student
        List<StudyPlan> currentPlans = studyPlanRepository.findByUserIdAndIsCurrent(userId, true);
        for (StudyPlan cp : currentPlans) {
            cp.setCurrent(false);
            studyPlanRepository.save(cp);
        }

        StudyPlan studyPlan = new StudyPlan(
                user,
                "Study Schedule (" + request.getStartDate() + " to " + request.getEndDate() + ")",
                request.getStartDate(),
                request.getEndDate(),
                0,
                ""
        );

        // Map to track allocated minutes per date
        Map<LocalDate, Integer> dayAllocations = new HashMap<>();
        Map<LocalDate, LocalTime> dayNextTime = new HashMap<>();

        LocalDate cur = request.getStartDate();
        while (!cur.isAfter(request.getEndDate())) {
            dayAllocations.put(cur, 0);
            dayNextTime.put(cur, LocalTime.of(9, 0)); // Start each day at 09:00 AM
            cur = cur.plusDays(1);
        }

        int totalAllocated = 0;
        int scheduledItemsCount = 0;
        List<String> unscheduledTaskSummaries = new ArrayList<>();
        StringBuilder explanation = new StringBuilder();
        explanation.append("Rule-Based Scheduling Execution Details:\n");
        explanation.append("- Planning Window: ").append(request.getStartDate()).append(" to ").append(request.getEndDate()).append("\n");
        explanation.append("- Daily Study Cap: ").append(dailyCap).append(" minutes\n");
        explanation.append("- Max Block Size: ").append(maxBlock).append(" minutes with ").append(breakBuffer).append(" minutes break\n");
        explanation.append("- Total Pending Tasks Analyzed: ").append(pendingTasks.size()).append("\n\n");

        for (StudyTask task : pendingTasks) {
            int remainingMinutes = task.getEstimatedMinutes() - (task.getActualMinutes() != null ? task.getActualMinutes() : 0);
            if (remainingMinutes <= 0) {
                continue;
            }

            LocalDate taskDeadlineDate = task.getDeadline().toLocalDate();
            int taskAllocated = 0;
            int blockIndex = 1;

            LocalDate allocDate = request.getStartDate();
            while (!allocDate.isAfter(request.getEndDate()) && !allocDate.isAfter(taskDeadlineDate) && remainingMinutes > 0) {
                int usedOnDay = dayAllocations.get(allocDate);
                int dayRemaining = dailyCap - usedOnDay;

                if (dayRemaining >= 25) { // Minimum practical study block is 25 minutes
                    int duration = Math.min(maxBlock, Math.min(remainingMinutes, dayRemaining));

                    LocalTime startTime = dayNextTime.get(allocDate);
                    LocalTime endTime = startTime.plusMinutes(duration);

                    StudyPlanItem item = new StudyPlanItem(
                            studyPlan,
                            task,
                            task.getTitle() + " (Block " + blockIndex + ")",
                            allocDate,
                            startTime,
                            endTime,
                            duration,
                            "Focus block for " + task.getTaskType() + " [" + task.getPriority() + " priority]"
                    );
                    studyPlan.addItem(item);

                    dayAllocations.put(allocDate, usedOnDay + duration);
                    dayNextTime.put(allocDate, endTime.plusMinutes(breakBuffer));

                    remainingMinutes -= duration;
                    taskAllocated += duration;
                    totalAllocated += duration;
                    scheduledItemsCount++;
                    blockIndex++;
                }

                allocDate = allocDate.plusDays(1);
            }

            if (remainingMinutes > 0) {
                unscheduledTaskSummaries.add(task.getTitle() + " (" + remainingMinutes + " mins unallocated before deadline " + taskDeadlineDate + ")");
            }
        }

        explanation.append("Scheduling Summary:\n");
        explanation.append("- Total Study Blocks Scheduled: ").append(scheduledItemsCount).append("\n");
        explanation.append("- Total Allocated Time: ").append(totalAllocated).append(" minutes (").append(String.format("%.1f", totalAllocated / 60.0)).append(" hours)\n");

        if (!unscheduledTaskSummaries.isEmpty()) {
            explanation.append("\nWARNING - Unscheduled Workload Detected:\n");
            for (String unscheduled : unscheduledTaskSummaries) {
                explanation.append("  * ").append(unscheduled).append("\n");
            }
            explanation.append("Recommendation: Increase daily study time or extend deadlines to cover remaining requirements.\n");
        } else {
            explanation.append("\nAll pending tasks were successfully scheduled within your designated study limits.\n");
        }

        studyPlan.setTotalAllocatedMinutes(totalAllocated);
        studyPlan.setExplanation(explanation.toString());

        StudyPlan savedPlan = studyPlanRepository.save(studyPlan);
        return StudyPlanResponse.fromEntity(savedPlan);
    }

    @Transactional(readOnly = true)
    public StudyPlanResponse getCurrentPlan(Long userId) {
        StudyPlan plan = studyPlanRepository.findByUserIdAndIsCurrentTrue(userId)
                .orElse(null);
        if (plan == null) {
            return null;
        }
        return StudyPlanResponse.fromEntity(plan);
    }

    @Transactional(readOnly = true)
    public PagedResponse<StudyPlanResponse> getPlanHistory(Long userId, Pageable pageable) {
        Page<StudyPlan> page = studyPlanRepository.findByUserIdOrderByGeneratedAtDesc(userId, pageable);
        List<StudyPlanResponse> responses = new ArrayList<>();
        for (StudyPlan plan : page.getContent()) {
            responses.add(StudyPlanResponse.fromEntity(plan));
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
    public StudyPlanItemResponse updatePlanItemStatus(Long userId, Long itemId, UpdatePlanItemStatusRequest request) {
        StudyPlanItem item = planItemRepository.findByIdAndStudyPlanUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyPlanItem", "id", itemId));

        item.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            item.setNotes(request.getNotes());
        }

        StudyPlanItem updated = planItemRepository.save(item);
        return StudyPlanItemResponse.fromEntity(updated);
    }

    private int getPriorityWeight(TaskPriority priority) {
        if (priority == null) return 2;
        switch (priority) {
            case URGENT:
                return 4;
            case HIGH:
                return 3;
            case MEDIUM:
                return 2;
            case LOW:
            default:
                return 1;
        }
    }

    private int getDifficultyWeight(TaskDifficulty difficulty) {
        if (difficulty == null) return 2;
        switch (difficulty) {
            case HARD:
                return 3;
            case MEDIUM:
                return 2;
            case EASY:
            default:
                return 1;
        }
    }
}
