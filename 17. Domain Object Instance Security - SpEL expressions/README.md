# Domain Object Instance Security - SpEL Expression

We have introduced fine grained access control using permissions in `HttpSecurity` configuration to secure each REST API. Then we moved them to secure the Service layer using Method security annotations as the recommended approach. Remember we assigned `PLAY_COURSE` and `UPDATE_COURSE` permissions to the `STUDENT` and `INSTRUCTOR` roles respectively. It allowed all the Students to play all the courses and all the Instructors to update all the courses. But our objective is

1. To play only the courses enrolled by the Student.
2. To update only the courses created by the Instructor.

So we need to enhance the security of these APIs in addition to the assigned permissions. Obviously we can implement the authorization check in our Spring Bean `ServiceSecurity` similar to what we saw in the [previous chapter](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/16.%20Method%20Security%20-%20Spring%20Beans):

```java
// Check if the course is created by the authenticated user
public boolean isCreatedBy(Authentication authentication, Long courseId) {
   Optional<Course> course = courseRepository.findById(courseId);
   if (course.isPresent()) {
      return course.get()
         .getCreatedBy()
         .getUsername()
         .equalsIgnoreCase(authentication.getName());
   }
   return false;
}
```

```java
// Check if the course is enrolled by the authenticated user
public boolean isEnrolledCourse(Authentication authentication, Long courseId) {
   Optional<AppUser> student = appUserRepository.findByUsername(authentication.getName());  
   if (student.isPresent()) {
      return student.get()
         .getEnrolledCourses()
         .stream()
         .anyMatch(course -> course.getId().equals(courseId));
   }
   return false;
}
```

The problem with this approach of dumping all sort of security helper methods will bloat the ServiceSecurity resulting in

Until now we secured the REST APIs (Web layer security) and the Service methods (Service layer security), but what we are dealing with now is securing the domain object instances i.e., Who can do what on a specific course object? And it requires a different approach.

In order to achieve domain object instance security, Spring Security offers below built-in SpEL expressions similar to `hasRole()` and `hasAuthority()`

```java
hasPermission(Object targetDomainObject, Object permission);

hasPermission(Serializable targetId, String targetType, Object permission);
```

We do not have to change the Service methods, we can just update with the below permission definitions for `UPDATE_COURSE` and `PLAY_COURSE` in `Authority` constants.

```java
"hasPermission(#courseId, T(com.thecodefacts.spring.security.domain.Course).getSimpleName(), T(com.thecodefacts.spring.security.enums.PermissionEnum).UPDATE_COURSE.name())";

"hasPermission(#courseId, T(com.thecodefacts.spring.security.domain.Course).getSimpleName(), T(com.thecodefacts.spring.security.enums.PermissionEnum).PLAY_COURSE.name())";
```

First parameter `#courseId` corresponds to the argument variable defined in the Service method to be annotated with `hasPermission()`, and it represents the unique `id` of the target domain object instance. Second parameter represents the type of the target domain object instance. We can use the domain class name as a String literal i.e., `Course` to represent the `targetType`, however we use the fully qualified name for type safety reasons. Third parameter is obvious that it is used to check whether the authenticated user has the given permission on the target domain object instance.

Now we have two below questions:

1. Who will implement the authorization check for the above SpEL expressions?
2. How do we get the authenticated user details to check for the given permission?

These expressions are basically delegated to an instance of `PermissionEvaluator` interface, which has two methods:

```java
  boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission);

  boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission);
```

These methods map directly to the above SpEL expression with the exception that the first argument (the `Authentication` object) will be passed by Spring Security.

`PermissionEvaluator` interface is intended to be the bridge between Spring's SpEL expression system and Spring Security's [ACL System](https://docs.spring.io/spring-security/reference/servlet/authorization/acls.html), allowing us to specify authorization checks on domain objects. It does not ship with Spring Security framework, it requires us to add `spring-security-acl-xxx.jar` as our mvn dependency.

## What is Spring Security's ACL?

Access Control List (ACL) is similar to the Permissions tab for each file resource in the Windows system when you open the properties. It maintains the list of users and their permissions on the respective file resource.

Similarly Spring Security's ACL system works based on the available permissions of each user for each domain object instances. This ACL information is stored in a database tables exclusively used by `spring-security-acl-xxx.jar`.

### Drawbacks of ACL
Spring Security's ACL is great and easier to write but it does not scale well for some of the following reasons.

1. Assume an application having 1 Million object instances as Objects, 1000 users as Subjects and 4 basic actions (CRUD) = 4 Billion ACL records. Imagine a query to search for an ACL record on these many records  for each Service method invocation. This can potentially cause performance issues and becomes a maintenance nightmare.
2. ACL records are basically driven based on the `identifiers` of the subject and the object mapped together with the permissions in the database table, not driven by the attributes of the subject and the object. This in itself have few practical issues.
   *a.* It is quite difficult to understand from the ACL table who can do what on which resource.
   *b.* Any change in the permission requires a huge insert/update/deletion of these ACL records corresponding to the impacted subject associated with the object under authorization. e.g., Assume `Spring Security Fundamentals` is a course under a topic named `Spring` grouped under a category named `Programming Languages`. And let's introduce a new Role `REVIEWER` with `UPDATE_COURSE`  permission on the courses with the topic in which the `REVIEWER` is tagged as an expert.

## Attribute-based Access Control (ABAC)
The drawbacks of ACL can be overcome by Attribute-based access control (ABAC) or Policy-based access control (PBAC). ABAC is a different and recently popular authorization mechanism to secure the domain object instances.

XACML is a specification to define and evaluate such attribute-based access control policies. We are not going to cover XACML here, we will then be deviating from Spring Security. If all your access control needs can be implemented by simple patterns and conditions within the context of your domain model, specifications like XACML will be an overkill for small and medium-sized applications.

Let's see how we are going to implement ABAC with Spring Security's `PermissionEvaluator` interface in the coming chapters.

***

Previous: [16. Method Security - Spring Beans](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/16.%20Method%20Security%20-%20Spring%20Beans)

Next: [18. Domain Object Instance Security - PermissionEvaluator](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/18.%20Domain%20Object%20Instance%20Security%20-%20PermissionEvaluator)