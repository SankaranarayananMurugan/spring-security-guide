package com.thecodefacts.spring.security.config;

import com.thecodefacts.spring.security.security.PermissionEvaluatorStrategyContext;
import com.thecodefacts.spring.security.service.DbUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    private PermissionEvaluatorStrategyContext permissionEvaluatorStrategyContext;

    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    @Autowired
    protected void configureUserDetailsService(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(dbUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler
                = new DefaultMethodSecurityExpressionHandler();
        defaultMethodSecurityExpressionHandler.setPermissionEvaluator(permissionEvaluatorStrategyContext);
        return defaultMethodSecurityExpressionHandler;
    }
}
