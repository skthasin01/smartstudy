package com.smartstudy.service;

import com.smartstudy.dto.task.TaskRequest;
import com.smartstudy.dto.task.TaskResponse;
import com.smartstudy.entity.Course;
import com.smartstudy.entity.StudyTask;
import com.smartstudy.entity.User;
import com.smartstudy.entity.enums.Role;
import com.smartstudy.entity.enums.TaskDifficulty;
import com.smartstudy.entity.enums.TaskPriority;
import com.smartstudy.entity.enums.TaskStatus;
import com.smartstudy.entity.enums.TaskType;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.CourseRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private StudyTaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private TaskService taskService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, courseRepository, userRepository);

        student = new User("student@example.com", "pass", "Student One", Role.ROLE_STUDENT);
        student.setId(1L);

        course = new Course(student, "CS101", "Intro to CS", "#4F46E5", "CS Course");
        course.setId(10L);
    }

    @Test
    @DisplayName("Create task should associate valid student course and default to TODO")
    void createTask_Success() {
        TaskRequest req = new TaskRequest(
                "Read Chapter 1",
                "Textbook reading",
                10L,
                TaskType.PREPARATION,
                TaskDifficulty.EASY,
                60,
                LocalDateTime.now().plusDays(3),
                TaskPriority.MEDIUM,
                TaskStatus.TODO
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(course));

        StudyTask created = new StudyTask(
                student, course, req.getTitle(), req.getDescription(),
                req.getTaskType(), req.getDifficulty(), req.getEstimatedMinutes(),
                req.getDeadline(), req.getPriority()
        );
        created.setId(100L);

        when(taskRepository.save(any(StudyTask.class))).thenReturn(created);

        TaskResponse res = taskService.createTask(1L, req);

        assertNotNull(res);
        assertEquals(100L, res.getId());
        assertEquals("Read Chapter 1", res.getTitle());
        assertEquals(10L, res.getCourseId());
        assertEquals("CS101", res.getCourseCode());
        assertEquals(TaskStatus.TODO, res.getStatus());
    }

    @Test
    @DisplayName("Create task should reject course not owned by the student")
    void createTask_CourseNotOwned_ThrowsBadRequest() {
        TaskRequest req = new TaskRequest(
                "Cheating Task",
                "Trying to use another user's course",
                999L,
                TaskType.ASSIGNMENT,
                TaskDifficulty.MEDIUM,
                60,
                LocalDateTime.now().plusDays(2),
                TaskPriority.HIGH,
                TaskStatus.TODO
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> taskService.createTask(1L, req));
    }

    @Test
    @DisplayName("Update task status to COMPLETED should set completed timestamp")
    void updateStatus_Completed_SetsCompletedAt() {
        StudyTask task = new StudyTask(
                student, course, "Algorithm Homework", "Solve 5 problems",
                TaskType.ASSIGNMENT, TaskDifficulty.HARD, 120,
                LocalDateTime.now().plusDays(2), TaskPriority.HIGH
        );
        task.setId(50L);

        when(taskRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(StudyTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse res = taskService.updateTaskStatus(1L, 50L, TaskStatus.COMPLETED);

        assertEquals(TaskStatus.COMPLETED, res.getStatus());
        assertNotNull(res.getCompletedAt());
    }

    @Test
    @DisplayName("Accessing non-existent or other student task throws ResourceNotFoundException")
    void getTask_UnauthorizedOrNotFound_ThrowsResourceNotFound() {
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(1L, 999L));
    }
}
