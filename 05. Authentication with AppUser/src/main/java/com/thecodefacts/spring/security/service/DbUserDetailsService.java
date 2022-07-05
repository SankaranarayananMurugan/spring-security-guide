package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbUserDetailsService {
    @Autowired
    private AppUserRepository appUserRepository;

    public List<UserDetails> getAllUserDetails() {
        return appUserRepository.findAll()
                .stream()
                .map(appUser -> User.builder()
                        .username(appUser.getUsername())
                        .password(String.format("{noop}%s", appUser.getPassword()))
                        .authorities(Collections.EMPTY_SET)
                        .build()
                )
                .collect(Collectors.toList());
    }
}
