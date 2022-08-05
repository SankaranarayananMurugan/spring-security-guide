package com.thecodefacts.spring.security.filters;

import com.thecodefacts.spring.security.config.JWTConfig;
import com.thecodefacts.spring.security.security.AuthenticationFacade;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenVerificationFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private JWTConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            // Parse JWT using the SecretKey
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.getSecretKey())
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            // Check if JWT has expired
            if (claims.getExpiration().after(new Date())) {
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                        claims.getSubject(), null, this.getAuthorities(claims)
                );
                authenticationFacade.setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> getAuthorities(Claims claims) {
        return ((List<String>) claims.get("authorities")).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
