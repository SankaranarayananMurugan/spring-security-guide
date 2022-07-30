package com.thecodefacts.spring.security.repo;

import com.thecodefacts.spring.security.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByTokenAndTokenExpiryTimeGreaterThan(String token, Date currentDate);
}
