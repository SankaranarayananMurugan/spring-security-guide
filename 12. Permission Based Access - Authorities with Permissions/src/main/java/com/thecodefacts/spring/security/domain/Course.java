package com.thecodefacts.spring.security.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.REFRESH;

@Entity(name = "course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String topic;

    private Double hours;

    private Double rating;

    @ManyToMany(mappedBy = "enrolledCourses")
    @JsonIgnore
    private Set<AppUser> enrolledStudents;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppUser createdBy;
}
