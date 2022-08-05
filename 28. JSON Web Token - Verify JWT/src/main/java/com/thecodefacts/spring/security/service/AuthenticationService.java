package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.config.JWTConfig;
import com.thecodefacts.spring.security.security.AuthenticationFacade;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private JWTConfig jwtConfig;

    public String generateToken(String username, String password) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authentication = authenticationManager.authenticate(authentication);

        String accessToken = null;
        if (authentication.isAuthenticated()) {
            authenticationFacade.setAuthentication(authentication);

            accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .setIssuedAt(jwtConfig.getIssueTime())
                    .setExpiration(jwtConfig.getExpiryTime(jwtConfig.getIssueTime()))
                    .setId(UUID.randomUUID().toString())
                    .addClaims(
                            Collections.singletonMap("authorities", authentication.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                            )
                    )
                    .signWith(jwtConfig.getSecretKey(), jwtConfig.getSignatureAlgorithm())
                    .compact();
        }

        return accessToken;
    }
}
