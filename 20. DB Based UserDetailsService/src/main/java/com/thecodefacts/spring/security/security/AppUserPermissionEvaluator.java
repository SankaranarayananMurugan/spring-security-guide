package com.thecodefacts.spring.security.security;

import com.thecodefacts.spring.security.domain.AppUser;
import com.thecodefacts.spring.security.enums.PermissionEnum;
import com.thecodefacts.spring.security.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;

@Component
public class AppUserPermissionEvaluator implements PermissionEvaluatorStrategy<AppUser> {
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject != null) {
            AppUser appUser = (AppUser) targetDomainObject;
            PermissionEnum permissionEnum = PermissionEnum.valueOf((String) permission);

            switch(permissionEnum) {
                case VIEW_PROFILE:
                    return this.isInstructor(appUser) || this.isSame(authentication, appUser);
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId != null) {
            Long appUserId = (Long) targetId;
            Optional<AppUser> appUser = appUserRepository.findById(appUserId);
            if (appUser.isPresent()) {
                return this.hasPermission(authentication, appUser.get(), permission);
            }
        }
        return false;
    }

    // Check if the requested appuser object have instructor role.
    public boolean isInstructor(AppUser appuser) {
        return appuser.getRoles()
                .stream()
                .anyMatch(appRole ->
                        appRole.getName().equals(INSTRUCTOR)
                );
    }

    // Check if the requested appuser object is same as authenticated user
    public boolean isSame(Authentication authentication, AppUser appuser) {
        return authentication.getName().equalsIgnoreCase(appuser.getUsername());
    }

    @Override
    public Class<AppUser> getTargetType() {
        return AppUser.class;
    }
}
