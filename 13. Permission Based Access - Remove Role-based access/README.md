# Permission Based Access - Remove Role-based Access

## Coarse grained access control

We know software requirements tends to change over a period of time. And this is applicable for non-functional requirements like Security as well. We gave coarse grained access control to a couple of APIs using Roles. Any change in the Security requirements in terms of securing the APIs will require us to modify the code by updating the `HttpSecurity` configuration.

For example, it is valid for a Student to view the list of instructors and get the list of courses filtered by the selected instructor. In this scenario, we have to secure the List Instructors API for both Admin and Student users as below:

```java
http  
	.csrf().disable()  
	.authorizeRequests(auth -> auth  
	        .antMatchers(GET, PUBLIC_API_LIST).permitAll()  
	        .antMatchers(API_LIST_STUDENTS).hasRole(ADMIN.name())  
	        .antMatchers(API_LIST_INSTRUCTORS).hasAnyRole(ADMIN.name(), STUDENT.name())  
	        .antMatchers(POST, API_CREATE_COURSES).hasRole(INSTRUCTOR.name())  
	        .antMatchers(API_PLAY_COURSE).hasAuthority(PLAY_COURSE.name())  
	        .anyRequest().authenticated()  
	)  
	.httpBasic();
```

## Fine grained access control

Permission grants the ability to perform an action on a resource. So we defined permissions in the format `ACTION_RESOURCENAME` which enables us to create fine grained access control to each resources.

Securing each API by defining its own permission and then granting those permissions to the roles offer four key benefits:

1. No more code modification, as granting permissions to roles is database driven.
2. Bird's eye view of who can do what in a single place, which is also database.
3. Fine grained access control to each API (and other resources).
4. Same permission-based access model can be used by both API as well as user interface, as every user action in the UI corresponds to an API execution in the application.

Let's change Role-based API access to Permission-based API access by changing the `HttpSecurity` configuration using `hasAuthority()` as below:

```java
http  
	.csrf().disable()  
	.authorizeRequests(auth -> auth  
	        .antMatchers(GET, PUBLIC_API_LIST).permitAll()  
	        .antMatchers(API_LIST_STUDENTS).hasAuthority(LIST_STUDENTS.name())  
	        .antMatchers(API_LIST_INSTRUCTORS).hasAuthority(LIST_INSTRUCTORS.name())
	        .antMatchers(API_VIEW_PROFILE).hasAuthority(VIEW_PROFILE.name())  
	        .antMatchers(POST, API_CREATE_COURSES).hasAuthority(CREATE_COURSE.name())
	        .antMatchers(PUT, API_UPDATE_COURSES).hasAuthority(UPDATE_COURSE.name())  
	        .antMatchers(API_PLAY_COURSE).hasAuthority(PLAY_COURSE.name())  
	        .anyRequest().authenticated()  
	)  
	.httpBasic();
```

With `hasAuthority()` Spring Security doesn't need to know about the roles of the user to authorize any API request. All it needs to know are only the Permissions or Authorities. So we can remove the role information from the `UserDetails` as given below: 

```java
appUserRepository.findAll()  
	.stream()  
	.map(appUser -> User.builder()  
	        .username(appUser.getUsername())  
	        .password(appUser.getPassword())  
	        .authorities(this.getPermissions(appUser.getRoles()))  
	        .build()  
	)  
	.collect(Collectors.toList());
```

```java
private String[] getPermissions(Set<AppRole> roles) {  
	return roles.stream()  
	        .flatMap(role -> role.getPermissions().stream())  
	        .map(permission -> permission.getName().name())  
	        .collect(Collectors.toSet())  
	        .toArray(new String[0]);  
}
```

This got even more simplified where we no longer required to combine roles (by prefixing with **ROLE_**) and permissions. And we no longer need to care about the authorities override issues we highlighted in [Chapter 7](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/07.%20Role%20Based%20Authorization).

As each API is secured against its own permission, and users will now only have the permissions corresponding to the APIs they can access as their authorities as mentioned in the below table.

| User | Authorities |
|--|--|
| Gru, Lucy | CREATE_COURSE <br/> UPDATE_COURSE <br/> PLAY_COURSE <br/> VIEW_PROFILE |
| Bob, Kevin, Stuart | PLAY_COURSE <br/> VIEW_PROFILE |
| Admin | LIST_STUDENTS <br/> LIST_INSTRUCTORS |

> **Note**
>
> 1. There will be no change in the API access behaviour after replacing the Role-based API access with Permission-based API access.
> 2. In addition to Play Course, we have secured Update Course as well as View Profile APIs with its own permissions, but still these three APIs have not met the Security Objectives defined in [Chapter 1](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/01.%20Introduction)

***

Previous: [12. Permission Based Access - Authorities with Permissions](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/12.%20Permission%20Based%20Access%20-%20Authorities%20with%20Permissions)

Next: [14. Method Security - PreAuthorize](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/14.%20Method%20Security%20-%20PreAuthorize)