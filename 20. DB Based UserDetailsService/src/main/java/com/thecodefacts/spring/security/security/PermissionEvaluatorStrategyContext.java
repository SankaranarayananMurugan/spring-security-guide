package com.thecodefacts.spring.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class PermissionEvaluatorStrategyContext implements PermissionEvaluator {
    @Autowired
    private List<PermissionEvaluatorStrategy> strategies;

    public PermissionEvaluator getPermissionEvaluator(String name) {
        return strategies.stream()
                .filter(strategy ->
                        strategy.getTargetType()
                                .getSimpleName()
                                .equalsIgnoreCase(name)
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("No permission evaluator found for the class %s", name)));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject != null) {
            String targetType = targetDomainObject.getClass().getSimpleName();
            if (hasAuthority(authentication, permission)) {
                return getPermissionEvaluator(targetType).hasPermission(authentication, targetDomainObject, permission);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId != null) {
            if (hasAuthority(authentication, permission)) {
                return getPermissionEvaluator(targetType).hasPermission(authentication, targetId, targetType, permission);
            }
        }
        return false;
    }

    private boolean hasAuthority(Authentication authentication, Object permission) {
        if (permission != null) {
            String strPermission = (String) permission;
            return authentication.getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority ->
                            grantedAuthority.getAuthority().equalsIgnoreCase(strPermission)
                    );
        }
        return false;
    }
}
