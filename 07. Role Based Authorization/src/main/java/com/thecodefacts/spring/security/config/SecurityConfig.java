package com.thecodefacts.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.thecodefacts.spring.security.constant.SecurityConstants.*;
import static com.thecodefacts.spring.security.enums.RoleEnum.ADMIN;
import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(auth -> auth
                        .antMatchers(GET, PUBLIC_API_LIST).permitAll()
                        .antMatchers(API_LIST_STUDENTS, API_LIST_INSTRUCTORS).hasRole(ADMIN.name())
                        .antMatchers(POST, API_CREATE_COURSES).hasRole(INSTRUCTOR.name())
                        .anyRequest().authenticated()
                )
                .httpBasic();
        return http.build();
    }
}
