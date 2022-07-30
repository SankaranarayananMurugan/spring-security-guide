package com.thecodefacts.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    public String generateToken(String username, String password) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authentication = authenticationManager.authenticate(authentication);

        String token = null;
        if (authentication.isAuthenticated()) {
            token = UUID.randomUUID().toString();
        }

        return token;
    }
}
