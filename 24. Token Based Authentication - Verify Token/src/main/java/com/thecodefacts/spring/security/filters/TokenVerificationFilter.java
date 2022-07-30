package com.thecodefacts.spring.security.filters;

import com.thecodefacts.spring.security.security.AuthenticationFacade;
import com.thecodefacts.spring.security.service.DbUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenVerificationFilter extends OncePerRequestFilter {
    @Autowired
    private DbUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");
            UserDetails userDetails = userDetailsService.loadUserByToken(token);

            if (userDetails != null) {
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                        userDetails.getUsername(), null, userDetails.getAuthorities()
                );
                authenticationFacade.setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
