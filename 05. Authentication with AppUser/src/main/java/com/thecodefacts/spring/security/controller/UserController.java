package com.thecodefacts.spring.security.controller;

import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;
import static com.thecodefacts.spring.security.enums.RoleEnum.STUDENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/students")
    public ResponseEntity<List<AppUser>> listStudents() {
        List<AppUser> studentList = userService.listByRoleName(STUDENT);
        log.debug("{} students found", studentList.size());
        return ResponseEntity.ok(studentList);
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<AppUser>> listInstructors() {
        List<AppUser> instructorList = userService.listByRoleName(INSTRUCTOR);
        log.debug("{} instructors found", instructorList.size());
        return ResponseEntity.ok(instructorList);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUser> get(@PathVariable("userId") Long userId) {
        AppUser appUser = userService.get(userId);
        if (appUser == null) {
            log.error("User with the given id {} not found", userId);
            return ResponseEntity.notFound().build();
        }
        log.debug("User with the given id {} found", userId);
        return ResponseEntity.ok(appUser);
    }
}
