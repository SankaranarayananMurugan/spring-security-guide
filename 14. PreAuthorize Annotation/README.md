# Method Security Annotation - PreAuthorize

[Spring Security](https://docs.spring.io/spring-security/reference/servlet/appendix/faq.html#appendix-faq-web-xml) mentions four security concerns to be addressed for any enterprise applications.

1. Authentication
2. Web request security
3. Service layer security
4. Domain object instance security

**Authentication**
Authentication is about identifying who the user is, so far we have used only Basic Auth as the authentication mechanism. We will see few more in the upcoming chapters.

**Web request security**
Web request security is about securing the request URLs. We did this using `antMatchers()` based on roles and permissions. For some reasons discussed [here](https://docs.spring.io/spring-security/reference/servlet/exploits/firewall.html#page-title) Spring Security recommends not to rely entirely on Web request security. Instead Security defined at Service layer is much more robust and harder to bypass.

**Service layer security**
Service layer security is about securing the service methods based on roles and permissions. This can be achieved by Spring Security's Method security annotation.

As recommended by Spring Security, let's restrict ourselves using simple antPatterns in the Web request security, and move the permission based access model to the Services layer using `@PreAuthorize`.

<hr/>

**@PreAuthorize**
`@PreAuthorize` annotation provides expression-based access control using SpEL (Spring Expression Language). It checks the given expression before entering the method.

Some of the valid `@PreAuthorize` annotations are:

`@PreAuthorize("hasRole('ROLE_ADMIN')")`

`@PreAuthorize("hasAuthority('LIST_STUDENTS')")`

Here, the expression resembles the method immediately following the `antMatchers()` in `HttpSecurity` configuration. We will create expressions using `hasAuthority()` with the appropriate `PermissionEnum` instances to secure the service methods using Permission-based model like below:

```java
@PreAuthorize("hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).PLAY_COURSE.name())")  
public Course play(Long courseId) {
}
```

SpEL expects the fully qualified name of the `PermissionEnum` instances. It may seem verbose but it is better to use Enums for type safety reasons rather than String literals. Any typo mistake in the expression using enums will throw Exception on the application startup.

SpEL expression must always be a String constant in `@PreAuthorize()`. We can move all the possible expressions in a constant class like below:

```java
public class Authority {  
    public static final String LIST_STUDENTS = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).LIST_STUDENTS.name())";
    public static final String LIST_INSTRUCTORS = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).LIST_INSTRUCTORS.name())";
    public static final String VIEW_PROFILE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).VIEW_PROFILE.name())";
    public static final String CREATE_COURSE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).CREATE_COURSE.name())";
    public static final String UPDATE_COURSE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).UPDATE_COURSE.name())";
    public static final String PLAY_COURSE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).PLAY_COURSE.name())";
}
```

Above `@PreAuthorize` annotation can now be simplified using the Constant.

```java
@PreAuthorize(Authority.PLAY_COURSE)  
public Course play(Long courseId) {
}
```

We can rewrite the same for other service methods as well. While doing so, we have to refactor `UserService.getRoleByName()` as it is more generic to apply specific permissions like `LIST_STUDENTS` and `LIST_INSTRUCTORS`. And also Controllers are not the right place to make business logic decisions, it can just do the request handling mechanism.

Let's make `getRoleByName()` private, and call the method from two different service methods using `RoleEnum.STUDENT` and `RoleEnum.INSTRUCTOR` respectively. Now we can secure these two service methods using specific permissions.

```java
@PreAuthorize(Authority.LIST_STUDENTS)  
public List<AppUser> listStudents() {  
    return this.listByRoleName(STUDENT);  
}  
  
@PreAuthorize(Authority.LIST_INSTRUCTORS)  
public List<AppUser> listInstructors() {  
    return this.listByRoleName(INSTRUCTOR);  
}
```

We can now remove the `antMatchers()` from `HttpSecurity` configuration and make it leaner as recommended by Spring Security

```java
http  
	.csrf().disable()  
	.authorizeRequests(auth -> auth  
	        .antMatchers(GET, PUBLIC_API_LIST).permitAll()  
	        .anyRequest().authenticated()  
	)  
	.httpBasic();
```

There will not be any change in the Security behaviour with the shift from Web request security to Service layer security. All the requests will be handled by the APIs now, but only authorized request makes into the Service method. Any unauthorized request will be thrown back with `403 Forbidden` error with the exception stack trace in the API response.

> **Note**
> You may often find developers using Method security annotations in Controller methods, though it is not restricted to do so. But Spring Security recommends applying them to the Service layer because Controller is simply the incorrect architectural layer to implement authorization decisions concerning services layer methods or domain object instances.