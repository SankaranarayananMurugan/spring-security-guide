package com.thecodefacts.spring.security.controller;

import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course) {
        Course createdCourse = courseService.create(course);
        log.debug("New course created with id: {}", createdCourse.getId());
        URI courseLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCourse.getId())
                .toUri();
        return ResponseEntity.created(courseLocation).body(createdCourse);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> update(@PathVariable("courseId") Long courseId, @RequestBody Course course) {
        Course updatedCourse = courseService.update(courseId, course);
        if (updatedCourse == null) {
            log.error("Course with the given id {} not found", courseId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Course with the given id {} is updated", updatedCourse.getId());
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping
    public ResponseEntity<List<Course>> list() {
        List<Course> courseList = courseService.list();
        log.debug("{} courses found", courseList.size());
        return ResponseEntity.ok(courseList);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> get(@PathVariable("courseId") Long courseId) {
        Course course = courseService.get(courseId);
        if (course == null) {
            log.error("Course with the given id {} not found", courseId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Course with the given id {} found", courseId);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/play/{courseId}")
    public ResponseEntity<Course> play(@PathVariable("courseId") Long courseId) {
        Course playingCourse = courseService.play(courseId);
        if (playingCourse == null) {
            log.error("Course with the given id {} not found", courseId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Playing course with the given id {}", courseId);
        return ResponseEntity.ok(playingCourse);
    }

}
