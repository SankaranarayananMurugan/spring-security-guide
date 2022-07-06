package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
                        .authorities(this.getRolesAndPermissions(appUser.getRoles()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private Set<String> getRoles(Set<AppRole> roles) {
        return roles.stream()
                .map(role -> String.format("ROLE_%s", role.getName().name()))
                .collect(Collectors.toSet());
    }

    private Set<String> getPermissions(Set<AppRole> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet());
    }

    private String[] getRolesAndPermissions(Set<AppRole> appRoles) {
        Set<String> roles = this.getRoles(appRoles);
        Set<String> permissions = this.getPermissions(appRoles);
        return new HashSet<String>() {
            {
                addAll(roles);
                addAll(permissions);
            }
        }.toArray(new String[0]);
    }

}
