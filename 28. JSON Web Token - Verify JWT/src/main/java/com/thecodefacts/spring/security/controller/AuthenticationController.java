package com.thecodefacts.spring.security.controller;

import com.thecodefacts.spring.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path = "token", consumes = {APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Map<String,String>> generateToken(@RequestParam("username") String username,
                             @RequestParam("password") String password) {
        String accessToken = authenticationService.generateToken(username, password);
        Map<String, String> tokenResponse = Collections.singletonMap("accessToken", accessToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
