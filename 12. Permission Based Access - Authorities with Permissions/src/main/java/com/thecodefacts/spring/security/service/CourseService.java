package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.repo.CourseRepository;
import com.thecodefacts.spring.security.security.AuthenticationFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public Course create(Course newCourse) {
        String username = authenticationFacade.getAuthentication().getName();
        AppUser currentUser = userService.get(username);
        newCourse.setCreatedBy(currentUser);
        return courseRepo.save(newCourse);
    }

    public Course update(Long courseId, Course course) {
        Course updatedCourse = null;
        Course existingCourse = courseRepo.findById(courseId).orElse(null);
        if (existingCourse != null) {
            BeanUtils.copyProperties(course, existingCourse, null, "id");
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

    public Course play(Long courseId) {
        return courseRepo.findById(courseId)
                .orElse(null);
    }
}
