# Permission Based Access - Permission Entity

In this chapter, we will cover the database aspects related to the `PermissionEnum` instances we introduced in the last chapter.

## Create AppPermission Entity

Create an `AppPermission` Entity having `name` of type `PermissionEnum`. As we can assign any number of permissions to each role, let's model this relationship as many-to-many with `AppRole` Entity.

```java
@Entity(name = "app_permission")  
@Data  
@Builder  
@NoArgsConstructor  
@AllArgsConstructor  
@EqualsAndHashCode(of = "id")  
public class AppPermission {  
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long id;  
    
    @Enumerated(value = EnumType.STRING)  
    private PermissionEnum name;  
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")  
    @JsonIgnore  
    private Set<AppRole> assignedRoles;  
}
```

## Update AppRole Entity

Now update the other side of the many-to-many relationship with `AppPermission` in the `AppRole` Entity, where `AppRole` Entity will be the owner of the relation.

```java
@ManyToMany(fetch = FetchType.EAGER)  
@JoinTable(
    name = "app_role_to_permission",  
    joinColumns = @JoinColumn(name = "app_role_id"),  
    inverseJoinColumns = @JoinColumn(name = "app_permission_id")  
)  
private Set<AppPermission> permissions;
```

## JPARepository for AppPermission Entity

Create a standard JPARepository interface for AppPermission entity:

```java
@Repository  
public interface AppPermissionRepository extends JpaRepository<AppPermission, Long> {  
}
```

## Assign Permissions to Roles

Update `AppDataInitialiser` in order to create Permission records for each `PermissionEnum` instances. In addition, assign those permissions to the roles as per the below table to meet the security objective defined in the first chapter.

| Role | Permission |
|--|--|
| Student | PLAY_COURSE, VIEW_PROFILE |
| Instructor | CREATE_COURSE, UPDATE_COURSE, PLAY_COURSE, VIEW_PROFILE |
| Admin | LIST_STUDENTS, LIST_INSTRUCTORS, VIEW_PROFILE |

> **Note**
> As we have changes in the `Entity` classes, in order to have those changes reflected, drop the database and re-create it before starting the application.

***

Previous: [10. Permission Based Access - Secure the APIs](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/10.%20Permission%20Based%20Access%20-%20Secure%20the%20APIs)

Next: [12. Permission Based Access - Authorities with Permissions](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/12.%20Permission%20Based%20Access%20-%20Authorities%20with%20Permissions)