package com.smartstudy.service;

import com.smartstudy.dto.course.CourseRequest;
import com.smartstudy.dto.course.CourseResponse;
import com.smartstudy.entity.Course;
import com.smartstudy.entity.User;
import com.smartstudy.exception.BadRequestException;
import com.smartstudy.exception.ResourceNotFoundException;
import com.smartstudy.repository.CourseRepository;
import com.smartstudy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCourses(Long userId) {
        List<Course> courses = courseRepository.findByUserIdOrderByCourseCodeAsc(userId);
        List<CourseResponse> responses = new ArrayList<>();
        for (Course course : courses) {
            responses.add(CourseResponse.fromEntity(course));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long userId, Long courseId) {
        Course course = courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        return CourseResponse.fromEntity(course);
    }

    @Transactional
    public CourseResponse createCourse(Long userId, CourseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String code = request.getCourseCode().trim().toUpperCase();
        if (courseRepository.existsByUserIdAndCourseCodeIgnoreCase(userId, code)) {
            throw new BadRequestException("You already have a course with code: " + code);
        }

        Course course = new Course(
                user,
                code,
                request.getCourseName().trim(),
                request.getColorCode(),
                request.getDescription()
        );

        Course saved = courseRepository.save(course);
        return CourseResponse.fromEntity(saved);
    }

    @Transactional
    public CourseResponse updateCourse(Long userId, Long courseId, CourseRequest request) {
        Course course = courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        String code = request.getCourseCode().trim().toUpperCase();
        if (!course.getCourseCode().equalsIgnoreCase(code) &&
                courseRepository.existsByUserIdAndCourseCodeIgnoreCase(userId, code)) {
            throw new BadRequestException("You already have a course with code: " + code);
        }

        course.setCourseCode(code);
        course.setCourseName(request.getCourseName().trim());
        if (request.getColorCode() != null) {
            course.setColorCode(request.getColorCode());
        }
        course.setDescription(request.getDescription());

        Course updated = courseRepository.save(course);
        return CourseResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        Course course = courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        courseRepository.delete(course);
    }
}
