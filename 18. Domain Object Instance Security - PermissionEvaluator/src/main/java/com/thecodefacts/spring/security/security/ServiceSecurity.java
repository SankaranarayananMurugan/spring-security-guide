package com.thecodefacts.spring.security.security;

import com.thecodefacts.spring.security.domain.AppUser;
import org.springframework.stereotype.Component;

import static com.thecodefacts.spring.security.enums.RoleEnum.INSTRUCTOR;

@Component("serviceSecurity")
public class ServiceSecurity {
    public Boolean isInstructor(AppUser appuser) {
        return appuser.getRoles()
                .stream()
                .anyMatch(appRole ->
                        appRole.getName().equals(INSTRUCTOR)
                );
    }
}
