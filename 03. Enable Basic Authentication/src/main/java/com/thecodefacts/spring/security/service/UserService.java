package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.enums.RoleEnum;
import com.thecodefacts.spring.security.repo.AppRoleRepository;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;
import static com.thecodefacts.spring.security.enums.RoleEnum.STUDENT;

@Service
public class UserService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppRoleRepository appRoleRepository;

    public List<AppUser> listStudents() {
        return this.listByRoleName(STUDENT);
    }

    public List<AppUser> listInstructors() {
        return this.listByRoleName(INSTRUCTOR);
    }

    private List<AppUser> listByRoleName(RoleEnum role) {
        AppRole appRole = appRoleRepository.findByName(role);
        return appRole.getUsers().stream()
                .sorted(
                        (appUser1, appUser2) ->
                        appUser1.getUsername().compareToIgnoreCase(appUser2.getUsername())
                )
                .collect(Collectors.toList());
    }

    public AppUser get(Long userId) {
        return appUserRepository.findById(userId)
                .orElse(null);
    }
}
