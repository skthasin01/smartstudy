package com.smartstudy.service;

import com.smartstudy.dto.common.PagedResponse;
import com.smartstudy.dto.task.TaskFilterRequest;
import com.smartstudy.dto.task.TaskRequest;
import com.smartstudy.dto.task.TaskResponse;
import com.smartstudy.entity.Course;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.CourseRepository;
import com.smartstudy.repository.StudyTaskRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final StudyTaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public TaskService(StudyTaskRepository taskRepository,
                       CourseRepository courseRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getTasks(Long userId, TaskFilterRequest filter, Pageable pageable) {
        Specification<StudyTask> spec = createSpecification(userId, filter);
        Page<StudyTask> taskPage = taskRepository.findAll(spec, pageable);

        List<TaskResponse> responses = new ArrayList<>();
        for (StudyTask task : taskPage.getContent()) {
            responses.add(TaskResponse.fromEntity(task));
        }

        return new PagedResponse<>(
                responses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long userId, Long taskId) {
        StudyTask task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse createTask(Long userId, TaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findByIdAndUserId(request.getCourseId(), userId)
                    .orElseThrow(() -> new BadRequestException("Specified course does not belong to you or does not exist"));
        }

        StudyTask task = new StudyTask(
                user,
                course,
                request.getTitle().trim(),
                request.getDescription(),
                request.getTaskType(),
                request.getDifficulty(),
                request.getEstimatedMinutes(),
                request.getDeadline(),
                request.getPriority()
        );
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        StudyTask saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, TaskRequest request) {
        StudyTask task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findByIdAndUserId(request.getCourseId(), userId)
                    .orElseThrow(() -> new BadRequestException("Specified course does not belong to you or does not exist"));
        }

        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setCourse(course);
        task.setTaskType(request.getTaskType());
        task.setDifficulty(request.getDifficulty());
        task.setEstimatedMinutes(request.getEstimatedMinutes());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());

        if (request.getStatus() != null && request.getStatus() != task.getStatus()) {
            task.setStatus(request.getStatus());
        }

        StudyTask updated = taskRepository.save(task);
        return TaskResponse.fromEntity(updated);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long userId, Long taskId, TaskStatus status) {
        StudyTask task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        task.setStatus(status);
        StudyTask updated = taskRepository.save(task);
        return TaskResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        StudyTask task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        taskRepository.delete(task);
    }

    private Specification<StudyTask> createSpecification(Long userId, TaskFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always enforce student ownership
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (filter != null) {
                if (StringUtils.hasText(filter.getSearch())) {
                    String searchPattern = "%" + filter.getSearch().toLowerCase().trim() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("title")), searchPattern),
                            cb.like(cb.lower(root.get("description")), searchPattern)
                    ));
                }

                if (filter.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), filter.getStatus()));
                }

                if (filter.getDifficulty() != null) {
                    predicates.add(cb.equal(root.get("difficulty"), filter.getDifficulty()));
                }

                if (filter.getPriority() != null) {
                    predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
                }

                if (filter.getCourseId() != null) {
                    predicates.add(cb.equal(root.get("course").get("id"), filter.getCourseId()));
                }

                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("deadline"), filter.getStartDate().atStartOfDay()));
                }

                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("deadline"), filter.getEndDate().atTime(23, 59, 59)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
