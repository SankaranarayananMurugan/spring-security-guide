package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DbUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(this.getPermissions(appUser.getRoles()))
                .build();
    }

    private String[] getPermissions(Set<AppRole> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName().name())
                .collect(Collectors.toSet())
                .toArray(new String[0]);
    }
}
