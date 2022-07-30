package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private UserService userService;

    public String generateToken(String username, String password) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authentication = authenticationManager.authenticate(authentication);

        String token = null;
        if (authentication.isAuthenticated()) {
            authenticationFacade.setAuthentication(authentication);

            token = UUID.randomUUID().toString();
            userService.updateToken(authentication.getName(), token, this.getTokenExpiryTime());
        }

        return token;
    }

    public Date getTokenExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);
        return calendar.getTime();
    }
}
