# Token Based Authentication - Invalidate Token

Any web application allows the user to voluntarily logout or expires their session (not necessarily HttpSession) in case of inactivity for a long time. This can be done by just getting rid of the token stored somewhere in the SPA and redirecting to the Login page. But the proper way to make this happen is to handle it with a dedicated and secured API to invalidate the token.

We will start by creating a `deleteToken()` in `UserService` which specifically updates the token and tokenExpiryTime to null for the authenticated user.

```java
@Service  
public class UserService {
    // Other methods omitted for brevity
    
    public void deleteToken(String username) {  
        AppUser appUser = this.get(username);  
        appUser.setToken(null);  
        appUser.setTokenExpiryTime(null);  
        appUserRepository.save(appUser);  
    }
}
```

Create an `invalidateToken()` in `AuthenticationService`, and call the `deleteToken()` by passing *username* retrieved from `Authentication` object representing the current authenticated user.

```java
@Service  
public class AuthenticationService {
    // Other methods omitted for brevity
    
    public void invalidateToken() {  
        userService.deleteToken(authenticationFacade.getAuthentication().getName());  
    }
}
```

Create an endpoint similar to `generateToken()` for `invalidateToken()` with `@DeleteMapping`, and call the above `AuthenticationService` method.

```java
@DeleteMapping("token")  
public ResponseEntity invalidateToken() {  
    authenticationService.invalidateToken();  
    return ResponseEntity.noContent().build();  
}
```

We do not need to change anything in `HttpSecurity`, as we have made `/auth/token` endpoint public only for `POST` method. So `DELETE` method is secured by default, as only authenticated user can logout to invalidate the token. Lastly we will secure the `deleteToken()` in `UserService` with same pre-authorize condition as in `updateToken()`, but with a different constant variable to explicitly tell the permission is only for deleting the token.

```java
@PreAuthorize(Authority.DELETE_TOKEN)  
public void deleteToken(String username) {
    ...
}
```

Restart the application, and generate a token for *Admin* user using GenerateToken API in Postman. Send a `DELETE` request to the same endpoint with the generated token as Bearer Token. This will reset the token and tokenExpiryTime column for the *Admin* user to null in the `app_user` table. We can no more use the same token for any protected APIs for the *Admin* user.

## Conclusion
With so many steps to implement token-based authentication mechanism, it is quite not simpler than the out of the box Basic Auth. Though it is quite effective and efficient and addresses most of the issues of Basic Auth but it also comes along with its own limitations. And this is not because of the authentication mechanism itself, but because of the type of the token we are using so far.

- These tokens are called **opaque tokens**, as the name implies we can not get any information out of the random string. There is no way to verify from the token to whom it was issued to (Subject).
- Single Page Applications (SPA) receiving this token can not decide what the user is allowed to do in the user interface from the token. It has to depend on a separate endpoint (in most cases it ends with url `/me` or `/profile`) to get the user profile and their permissions. These tokens can only be used as a means of authentication and not for authorization.
- As these are opaque tokens, it should always be backed by a persistent datastore. In our case we had to associate the token with the user and it's expiry time in the database along with the `AppUser` record.
- API request spanning multiple microservices, where the token has to be relayed across requires a round trip to the datastore just to validate the token in each microservice.