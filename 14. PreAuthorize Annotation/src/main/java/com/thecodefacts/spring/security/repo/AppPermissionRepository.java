package com.thecodefacts.spring.security.repo;

import com.thecodefacts.spring.security.domain.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppPermissionRepository extends JpaRepository<AppPermission, Long> {
}
