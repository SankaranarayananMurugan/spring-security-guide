package com.thecodefacts.spring.security.config;

import com.thecodefacts.spring.security.filters.TokenVerificationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.thecodefacts.spring.security.constant.SecurityConstants.API_AUTH_TOKEN;
import static com.thecodefacts.spring.security.constant.SecurityConstants.PUBLIC_API_LIST;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class ApiSecurityConfig {
    @Autowired
    private TokenVerificationFilter tokenVerificationFilter;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(tokenVerificationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests(auth -> auth
                        .antMatchers(POST, API_AUTH_TOKEN).permitAll()
                        .antMatchers(GET, PUBLIC_API_LIST).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
