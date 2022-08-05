package com.thecodefacts.spring.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thecodefacts.spring.security.enums.RoleEnum;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "app_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private RoleEnum name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    @JsonIgnore
    private Set<AppUser> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_role_to_permission",
            joinColumns = @JoinColumn(name = "app_role_id"),
            inverseJoinColumns = @JoinColumn(name = "app_permission_id")
    )
    private Set<AppPermission> permissions;
}
