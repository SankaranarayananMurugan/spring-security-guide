package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.repo.CourseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    public Course create(Course newCourse) {
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
