# Token Based Authentication - Persist Token

As we mentioned in the last chapter, we have to associate the generated token with the user by storing it somewhere to make it available for verification on subsequent requests. Storing in-memory is a bad idea as we will lose all the generated tokens and its associations, on restarting the application which will then force all the user to login again. This will also not work in multiple instance scenarios as well.

We will store these tokens in the database along with the user record which requires us to modify our `AppUser` entity to store the token and it's expiry time. So let's add them as below:

```java
public class AppUser {
    // Other fields omitted for brevity
    
    private String token;  
    
    private Date tokenExpiryTime;
}
```

Now we will implement `updateToken()` method in `UserService` component to update only the token and it's expiry time for the given user. We can identify the `AppUser` by the *username* using the existing `get()` method.

```java
public void updateToken(String username, String token, Date tokenExpiryTime) {  
    AppUser appUser = this.get(username);  
    appUser.setToken(token);  
    appUser.setTokenExpiryTime(tokenExpiryTime);  
    appUserRepository.save(appUser);  
}
```

Now we have everything in place to persist the token. So let's autowire the `UserService` component in `AuthenticationService` and call its `updateToken()` method once the authentication is success.

```java
if (authentication.isAuthenticated()) {  
  token = UUID.randomUUID().toString();  
  userService.updateToken(authentication.getName(), token, this.getTokenExpiryTime());  
}
```

Restart the application to automatically update the `app_user` table with the newly added two columns. Test the GenerateToken API to see the generated token persisted for the given user in the `app_user` table.

Are we done with persisting the token? Not Completely. We should never let anyone calling the `updateToken()` in `UserService` anonymously. We have to secure every service method as part of Service Layer Security. In this case we want only the user who requested the token to update his record with the generated token.

Let's define the pre-authorize condition in `SecurityConstants.Authority` class like below. Here `#username` represents one of the arguments with the same name defined in `updateToken()` method. And `authentication` represents the current authenticated user, which is provided automatically by Spring Security from `SecurityContext`.

```java
public static final String UPDATE_TOKEN = "#username == authentication.name";
```

Then add `@PreAuthorize` annotation on the `updateToken()` with the above condition.

```java
@PreAuthorize(Authority.UPDATE_TOKEN)  
public void updateToken(String username, String token, Date tokenExpiryTime) {
    // Details omitted for brevity
}
```

In our case it is we who are authenticating the user by calling `AuthenticationManager.authenticate()` by ourselves, so `SecurityContext` in no way is aware of the `authentication` object . Unlike Basic Auth, where everything was done out of the box by Spring Security for us, here we have to explicitly set the `SecurityContext` with the authenticated `authentication` object.

Let's define another method in `AuthenticationFacade` to set the `SecurityContext` with the authenticated `Authentication` object

```java
@Component  
public class AuthenticationFacade {  
    public Authentication getAuthentication() {  
        return SecurityContextHolder.getContext().getAuthentication();  
    }  
    
    public void setAuthentication(Authentication authentication) {  
        SecurityContextHolder.getContext().setAuthentication(authentication);  
    }  
}
```

Lastly autowire the `AuthenticationFacade` in `AuthenticationService` and set the `SecurityContext` before calling the `updateToken()` method in `UserService`. The final `generateToken()` thus looks like below

```java
public String generateToken(String username, String password) {  
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);  
    authentication = authenticationManager.authenticate(authentication);  
    
    String token = null;  
    if (authentication.isAuthenticated()) {  
        authenticationFacade.setAuthentication(authentication);  
        
        token = UUID.randomUUID().toString();  
        userService.updateToken(authentication.getName(), token, this.getTokenExpiryTime());  
    }  
    
    return token;  
}
```