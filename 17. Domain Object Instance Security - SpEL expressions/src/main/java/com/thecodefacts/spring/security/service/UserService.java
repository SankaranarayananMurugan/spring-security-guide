package com.thecodefacts.spring.security.service;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.enums.RoleEnum;
import com.thecodefacts.spring.security.repo.AppRoleRepository;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.thecodefacts.spring.security.constant.SecurityConstants.Authority;
import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;
import static com.thecodefacts.spring.security.enums.RoleEnum.STUDENT;

@Service
public class UserService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppRoleRepository appRoleRepository;

    @PreAuthorize(Authority.LIST_STUDENTS)
    public List<AppUser> listStudents() {
        return this.listByRoleName(STUDENT);
    }

    @PreAuthorize(Authority.LIST_INSTRUCTORS)
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

    @PreAuthorize(Authority.VIEW_PROFILE)
    @PostAuthorize("@serviceSecurity.isInstructor(returnObject) or returnObject.username == authentication.name")
    public AppUser get(Long userId) {
        return appUserRepository.findById(userId)
                .orElse(null);
    }

    @PreAuthorize(Authority.VIEW_PROFILE)
    @PostAuthorize("@serviceSecurity.isInstructor(returnObject) or returnObject.username == authentication.name")
    public AppUser get(String username) {
        return appUserRepository.findByUsername(username);
    }
}
