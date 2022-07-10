package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
                        .password(appUser.getPassword())
                        .authorities(this.getPermissions(appUser.getRoles()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private String[] getPermissions(Set<AppRole> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet())
                .toArray(new String[0]);
    }

}
