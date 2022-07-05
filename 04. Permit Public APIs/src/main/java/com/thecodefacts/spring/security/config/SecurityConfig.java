package com.thecodefacts.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.thecodefacts.spring.security.constant.SecurityConstants.*;
import static org.springframework.http.HttpMethod.GET;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(auth -> auth
                        .antMatchers(GET, PUBLIC_API_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic();
        return http.build();
    }
}
