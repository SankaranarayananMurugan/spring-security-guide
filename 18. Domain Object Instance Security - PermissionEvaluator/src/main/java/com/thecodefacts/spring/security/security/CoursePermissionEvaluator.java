package com.thecodefacts.spring.security.security;

import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.enums.PermissionEnum;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import com.thecodefacts.spring.security.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class CoursePermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject != null) {
            Course course = (Course) targetDomainObject;
            PermissionEnum permissionEnum = PermissionEnum.valueOf((String) permission);

            switch(permissionEnum) {
                case UPDATE_COURSE:
                    return this.isCreatedBy(authentication, course);
                case PLAY_COURSE:
                    return this.isEnrolledStudent(authentication, course.getId());
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId != null) {
            Long courseId = (Long) targetId;
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isPresent()) {
                return this.hasPermission(authentication, course.get(), permission);
            }
        }
        return false;
    }

    // Check if the requested course is created by the authenticated user.
    private boolean isCreatedBy(Authentication authentication, Course course) {
        return course.getCreatedBy().getUsername().equalsIgnoreCase(authentication.getName());
    }

    // Check if the requested course is enrolled by the authenticated user.
    private boolean isEnrolledStudent(Authentication authentication, Long courseId) {
        Optional<AppUser> student = appUserRepository.findByUsername(authentication.getName());
        if (student.isPresent()) {
            return student.get()
                    .getEnrolledCourses()
                    .stream()
                    .anyMatch(course -> course.getId().equals(courseId));
        }
        return false;
    }
}
