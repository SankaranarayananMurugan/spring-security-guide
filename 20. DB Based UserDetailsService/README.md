# DB Based UserDetailsService

We are still using `InMemoryUserDetailsManager` which is backed by an in-memory map, mainly intended for testing and demonstration purposes. We need an implementation of `UserDetailsService` backed by database for production implementation. We can do so easily with minor changes as we are already using database to load all the users into `InMemoryUserDetailsManager`.

First of all remove `userDetailsService()` bean method which creates `InMemoryUserDetailsManager` object in `SecurityBean`.

```java
@Configuration  
public class SecurityBean {  
    @Bean  
    public PasswordEncoder passwordEncoder() {  
        return new BCryptPasswordEncoder();  
    }  
}
```

Update `DbUserDetailsService` to implement `UserDetailsService` interface which requires us to implement only one method `loadUserByUsername()`. And remove `loadAllUserDetails()` as we no longer require it.

```java
@Override  
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));

    return User.builder()
            .username(appUser.getUsername())
            .password(appUser.getPassword())
            .authorities(this.getPermissions(appUser.getRoles()))
            .build();
}
```

Update `SecurityConfig` class with an `@Autowired` method to configure the global `AuthenticationManagerBuilder` with `DbUserDetailsService` and `PasswordEncoder` beans.

```java
@Autowired  
protected void configureUserDetailsService(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {  
    auth.userDetailsService(dbUserDetailsService).passwordEncoder(passwordEncoder);  
}
```

Restart the application to see Spring Security using `DbUserDetailsService` to load the user details using `loadUserByUsername()` and all the APIs behaving with no visible changes.