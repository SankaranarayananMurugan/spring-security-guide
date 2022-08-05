package com.thecodefacts.spring.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity(name = "app_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_user_to_role",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "app_role_id")
    )
    private Set<AppRole> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "app_user_to_course",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> enrolledCourses;

    @OneToMany(mappedBy = "createdBy", cascade = ALL)
    @JsonIgnore
    private Set<Course> createdCourses;
}
