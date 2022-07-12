package com.thecodefacts.spring.security.repo;

import com.thecodefacts.spring.security.domain.AppRole;
import com.thecodefacts.spring.security.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByName(RoleEnum name);
}
