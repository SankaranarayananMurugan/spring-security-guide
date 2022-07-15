# Domain Object Instance Security - PermissionEvaluator Strategy

We need a way to choose from multiple `PermissionEvaluator` implementations based on the type of the `targetObject` under check. **Strategy Behavioural Pattern** lets us switch between multiple strategies at runtime based on the encapsulated strategy object(s) in the `StrategyContext`.

Let's create a `Component` class `PermissionEvaluatorStrategyContext` implementing `PermissionEvaluator` interface.

```java
@Component
public class PermissionEvaluatorStrategyContext implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
```

Create a private `hasAuthority()` and check if the authenticated user have the requested permission as one of his `GrantedAuthority` objects before checking it for the `targetObject`.

```java
private boolean hasAuthority(Authentication authentication, Object permission) {
    if (permission != null) {
        String strPermission = (String) permission;
        return authentication.getAuthorities()
            .stream()
            .anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equalsIgnoreCase(strPermission)
            );
    }
    return false;
}
```

Create below `Strategy` interface extending `PermissionEvaluator` interface.

```java
public interface PermissionEvaluatorStrategy<T> extends PermissionEvaluator {
    <T> Class<T> getTargetType();
}
```

We created it as a Generic interface where the parameterised type in the implementation class must represent the domain class for which the authorization check is going to be implemented. It must also implement `getTargetType()` returning the type `Class<T>` of the domain class.

Let's update `CoursePermissionEvaluator` and `AppUserPermissionEvaluator` to implement the above `PermissionEvaluatorStrategy` instead of `PermissionEvaluator` interface as the latter is already extended by the former.

```java
@Component
public class CoursePermissionEvaluator implements PermissionEvaluatorStrategy<Course> {
	... // No change in the hasPermission() overloaded methods

    @Override
    public Class<Course> getTargetType() {
        return Course.class;
    }
}
```

```java
@Component
public class AppUserPermissionEvaluator implements PermissionEvaluatorStrategy<AppUser> {
	... // No change in the hasPermission() overloaded methods

    @Override
    public Class<AppUser> getTargetType() {
        return AppUser.class;
    }
}
```

Declare a list of `PermissionEvaluatorStrategy` variable and annotate with `@Autowired` inside `PermissionEvaluatorStrategyContext`. Spring will then autowire all the Beans implementing the interface. Create a private method to filter the right `PermissionEvaluatorStrategy` implementation by comparing the class name returned by the `getTargetType()` of the implemented strategies with the given argument.

```java
@Autowired
private List<PermissionEvaluatorStrategy> strategies;

public PermissionEvaluator getPermissionEvaluator(String name) {
    return strategies.stream()
        .filter(strategy ->
            strategy.getTargetType()
                .getSimpleName()
                .equalsIgnoreCase(name)
        )
        .findFirst()
        .orElseThrow(() -> new RuntimeException(String.format("No permission evaluator found for the class %s", name)));
}
```

Finally let's update the `hasPermission()` overloaded methods to wire them all together as below:

```java
@Override
public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
    if (targetDomainObject != null) {
        String targetType = targetDomainObject.getClass().getSimpleName();
        if (hasAuthority(authentication, permission)) {
            return getPermissionEvaluator(targetType).hasPermission(authentication, targetDomainObject, permission);
        }
    }
    return false;
}
```

```java
@Override
public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    if (targetId != null) {
        if (hasAuthority(authentication, permission)) {
            return getPermissionEvaluator(targetType).hasPermission(authentication, targetId, targetType, permission);
        }
    }
    return false;
}
```

> Note
> Don't forget to update the `SecurityConfig` to wire the `PermissionEvaluatorStrategyContext` instead of `CoursePermissionEvaluator` before testing the APIs.

We achieved more fine-grained access control on the domain object instances using the security checks based on the attributes of the subject and the object. This is a simple and efficient solution to achieve ABAC using Spring's built-in SpEL capability. From the `PermissionEvaluator` implementations for each domain class we can easily understand who can do what on them. It is easier to manage the permissions as each implementation own the permissions related to only one domain class defined in the parameterized type.

In a Microservice architecture, each microservice owns only a minor subset of the larger Business domain. And the team who manages each microservice owns the resposibility to secure these domain object instances. Those who does not want to centralize and externalize the authorization policies in these scenarios can define and implement their own domain model permissions local to each domain class in its own `PermissionEvaluator`.

## Conclusion (Authorization)

We explored below techniques to secure different layers, and now we came to an epic conclusion part on Authorization.

1. `Httpsecurity` for Web layer security.
2. `Method Security Annotations` for Service layer security.
3. `ABAC technique using Method Security Annotation`  for Domain object instance security.

So far we have addressed three out of four security concerns for any enterprise application. Of course we visited Authentication by enabling `Basic Authentication` at the start of the course and used until now to identify the user. We will continue to see various other authentication mechanisms in the upcoming chapters.

Before that let us replace `InMemoryUserDetailsManager` with our own `UserDetailsService` implementation in the next chapter.

Previous: [18. Domain Object Instance Security - PermissionEvaluator](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/18.%20Domain%20Object%20Instance%20Security%20-%20PermissionEvaluator)

Next: [20. DB Based UserDetailsService](https://github.com/SankaranarayananMurugan/spring-security-guide/tree/main/20.%20DB%20Based%20UserDetailsService)