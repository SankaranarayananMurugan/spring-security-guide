package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.repo.CourseRepository;
import com.thecodefacts.spring.security.security.AuthenticationFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.thecodefacts.spring.security.constant.SecurityConstants.Authority;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @PreAuthorize(Authority.CREATE_COURSE)
    public Course create(Course newCourse) {
        String username = authenticationFacade.getAuthentication().getName();
        AppUser currentUser = userService.get(username);
        newCourse.setCreatedBy(currentUser);
        return courseRepo.save(newCourse);
    }

    @PreAuthorize(Authority.UPDATE_COURSE)
    public Course update(Long courseId, Course course) {
        Course updatedCourse = null;
        Course existingCourse = courseRepo.findById(courseId).orElse(null);
        if (existingCourse != null) {
            BeanUtils.copyProperties(course, existingCourse, "id");
            updatedCourse = courseRepo.save(existingCourse);
        }
        return updatedCourse;
    }

    public List<Course> list() {
        return courseRepo.findAll();
    }

    public Course get(Long courseId) {
        return courseRepo.findById(courseId)
                .orElse(null);
    }

    @PreAuthorize(Authority.PLAY_COURSE)
    public Course play(Long courseId) {
        return courseRepo.findById(courseId)
                .orElse(null);
    }
}
