package com.thecodefacts.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.thecodefacts.spring.security.constant.SecurityConstants.*;
import static com.thecodefacts.spring.security.enums.PermissionEnum.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(auth -> auth
                        .antMatchers(GET, PUBLIC_API_LIST).permitAll()
                        .antMatchers(API_LIST_STUDENTS).hasAuthority(LIST_STUDENTS.name())
                        .antMatchers(API_LIST_INSTRUCTORS).hasAuthority(LIST_INSTRUCTORS.name())
                        .antMatchers(API_VIEW_PROFILE).hasAuthority(VIEW_PROFILE.name())
                        .antMatchers(POST, API_CREATE_COURSES).hasAuthority(CREATE_COURSE.name())
                        .antMatchers(PUT, API_UPDATE_COURSES).hasAuthority(UPDATE_COURSE.name())
                        .antMatchers(API_PLAY_COURSE).hasAuthority(PLAY_COURSE.name())
                        .anyRequest().authenticated()
                )
                .httpBasic();
        return http.build();
    }
}
