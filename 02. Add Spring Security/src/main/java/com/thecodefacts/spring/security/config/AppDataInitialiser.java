package com.thecodefacts.spring.security.config;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.domain.Course;
import com.thecodefacts.spring.security.repo.AppRoleRepository;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import com.thecodefacts.spring.security.repo.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thecodefacts.spring.security.enums.RoleEnum.*;

@Slf4j
@Component
public class AppDataInitialiser implements ApplicationRunner {
    @Autowired
    private AppRoleRepository appRoleRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.clearAppData();

        AppRole[] roles = this.createRoles();
        AppUser[] instructors = this.createInstructors(roles[1]);
        Course[] courses = this.createCourses(instructors);
        AppUser[] students = this.createStudents(roles[0], courses);
        AppUser admin = this.createAdmin(roles[2]);
    }

    private void clearAppData() {
        appUserRepository.deleteAll();
        appRoleRepository.deleteAll();
        courseRepository.deleteAll();
    }

    private AppRole[] createRoles() {
        AppRole studentRole = AppRole.builder().name(STUDENT).build();
        appRoleRepository.save(studentRole);

        AppRole instructorRole = AppRole.builder().name(INSTRUCTOR).build();
        appRoleRepository.save(instructorRole);

        AppRole adminRole = AppRole.builder().name(ADMIN).build();
        appRoleRepository.save(adminRole);

        return new AppRole[] { studentRole, instructorRole, adminRole};
    }

    private AppUser[] createInstructors(AppRole instructorRole) {
        AppUser gruUser = AppUser.builder()
                .username("Gru")
                .password("password")
                .email("gru@email.com")
                .roles(Collections.singleton(instructorRole))
                .build();
        appUserRepository.save(gruUser);

        AppUser lucyUser = AppUser.builder()
                .username("Lucy")
                .password("password")
                .email("lucy@email.com")
                .roles(Collections.singleton(instructorRole))
                .build();
        appUserRepository.save(lucyUser);

        return new AppUser[] { gruUser, lucyUser};
    }

    private Course[] createCourses(AppUser... instructorUsers) {
        AppUser gruUser = instructorUsers[0];
        AppUser lucyUser = instructorUsers[1];

        Course springBootCourse = Course.builder()
                .name("Spring Boot Fundamentals")
                .category("Programming")
                .topic("Spring")
                .hours(5D)
                .rating(4.5D)
                .createdBy(gruUser)
                .build();
        courseRepository.save(springBootCourse);

        Course springSecurityCourse = Course.builder()
                .name("Secure REST APIs with Spring Security")
                .category("Programming")
                .topic("Spring Security")
                .hours(1.5D)
                .rating(4D)
                .createdBy(lucyUser)
                .build();
        courseRepository.save(springSecurityCourse);

        Course springDataJpaCourse = Course.builder()
                .name("Master Spring Data JPA")
                .category("Programming")
                .topic("Spring Data")
                .hours(3.5D)
                .rating(5D)
                .createdBy(gruUser)
                .build();
        courseRepository.save(springDataJpaCourse);

        Course springMicroserviceCourse = Course.builder()
                .name("Spring Boot Microservices and Spring Cloud")
                .category("Technology")
                .topic("Microservice")
                .hours(15D)
                .rating(5D)
                .createdBy(lucyUser)
                .build();
        courseRepository.save(springMicroserviceCourse);

        return new Course[] { springBootCourse, springSecurityCourse, springDataJpaCourse, springMicroserviceCourse };
    }

    private AppUser[] createStudents(AppRole studentRole, Course... courses) {
        Course springBootCourse = courses[0];
        Course springSecurityCourse = courses[1];
        Course springDataJpaCourse = courses[2];
        Course springMicroserviceCourse = courses[3];

        AppUser bobUser = AppUser.builder()
                .username("Bob")
                .password("password")
                .email("bob@email.com")
                .roles(Collections.singleton(studentRole))
                .enrolledCourses(
                        Stream.of(springBootCourse, springMicroserviceCourse)
                                .collect(Collectors.toSet())
                )
                .build();
        appUserRepository.save(bobUser);

        AppUser kevinUser = AppUser.builder()
                .username("Kevin")
                .password("password")
                .email("kevin@email.com")
                .roles(Collections.singleton(studentRole))
                .enrolledCourses(
                        Stream.of(springBootCourse, springSecurityCourse)
                                .collect(Collectors.toSet())
                )
                .build();
        appUserRepository.save(kevinUser);

        AppUser stuartUser = AppUser.builder()
                .username("Stuart")
                .password("password")
                .email("stuart@email.com")
                .roles(Collections.singleton(studentRole))
                .enrolledCourses(
                        Stream.of(springBootCourse, springDataJpaCourse, springSecurityCourse)
                                .collect(Collectors.toSet())
                )
                .build();
        appUserRepository.save(stuartUser);

        return new AppUser[] { bobUser, kevinUser, stuartUser };
    }

    private AppUser createAdmin(AppRole adminRole) {
        AppUser adminUser = AppUser.builder()
                .username("Admin")
                .password("password")
                .email("admin@email.com")
                .roles(Collections.singleton(adminRole))
                .build();
        appUserRepository.save(adminUser);
        return adminUser;
    }
}
