# JSON Web Token - Generate JWT

Our first step is to replace the random UUID token generation with JWT. But before generating the JWT, we will add some useful configuration in the properties and get it via `@ConfigurationProperties` class.

```
jwt.token.base64-encoded-secret-key=/EbiawupzOqD8MlXgzlRetQfLL5vbD65jE6Q2MJF/Gg=
jwt.token.expiry-in-seconds=600
jwt.token.signing-algorithm=HS256
```

These properties need no explanation except for the first one. JWT must be signed with a strong secret key based on the signing algorithm we choose, else the signing algorithm will throw *Weak secret key* error. JJWT provides a utility to generate this `SecretKey` for us. We can Base64 encode it to a string and store it in a secure place and not in properties.

I have included below test class in this chapter, and this can be used to generate Base64 encoded SecretKey, and it also assert the equality of the decoded SecretKey with the original one.

```java
public class SecretKeyGeneratorTest {
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    @Test
    public void generateBase64EncodedSecretKeyString() {
        // Generate and Base64 encode to string to store it in disk  
        SecretKey originalKey = Keys.secretKeyFor(ALGORITHM);
        String base64EncodedKeyString = Base64.getEncoder().encodeToString(originalKey.getEncoded());
        System.out.println("Base64 encoded secret key generated below, store it in a secure place");
        System.out.println(base64EncodedKeyString);

        // Base64 decode from string and regenerate SecretKey
        byte[] base64DecodedKeyBytes = Base64.getDecoder().decode(base64EncodedKeyString);
        SecretKey regeneratedKey = new SecretKeySpec(base64DecodedKeyBytes, 0, base64DecodedKeyBytes.length, ALGORITHM.getJcaName());

        assert originalKey.equals(regeneratedKey);
    }
}
```

Let's create a `@ConfigurationProperties` class wth name `JWTConfig`.

```java
@Configuration
@ConfigurationProperties(prefix = "jwt.token")
@Setter
public class JWTConfig {
    private String base64EncodedSecretKey;

    private Integer expiryInSeconds;

    private String signingAlgorithm;
} 
```

Add a method to regenerate `SecretKey` object from the Base64 encoded string in the properties similar to the above test class.

```java
public SecretKey getSecretKey() {
    byte[] base64DecodedKeyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
    SecretKey secretKey = new SecretKeySpec(base64DecodedKeyBytes, 0,
    base64DecodedKeyBytes.length, this.getSignatureAlgorithm().getJcaName());
    return secretKey;
}
```

Add another method to get signing algorithm object of type `SignatureAlgorithm` from the signing-algorithm properties.

```java
public SignatureAlgorithm getSignatureAlgorithm() {
    return SignatureAlgorithm.valueOf(signingAlgorithm);
}
```

Add couple more methods to get token issue time (current time) and expiry time (issue time + expiry-in-seconds). In our case we have configured the token expiry time to be 600 seconds from the issue time.

```java
public Date getIssueTime() {
    return new Date(System.currentTimeMillis());
}

public Date getExpiryTime(Date issueDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(issueDate);
    calendar.add(Calendar.SECOND, expiryInSeconds);
    return calendar.getTime();
}
```

Finally we will replace the random UUID token generation in `AuthenticationService` with below JWT generation using `JwtBuilder` class provided by `Jwts`.

```java
public String generateToken(String username, String password) {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    authentication = authenticationManager.authenticate(authentication);
    
    String accessToken = null;
    if (authentication.isAuthenticated()) {
        authenticationFacade.setAuthentication(authentication);
        
        // Generate JWT
        accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .setIssuedAt(jwtConfig.getIssueTime())
            .setExpiration(jwtConfig.getExpiryTime(jwtConfig.getIssueTime()))
            .setId(UUID.randomUUID().toString())
            .addClaims(
                Collections.singletonMap("authorities", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                )
            )
            .signWith(jwtConfig.getSecretKey(), jwtConfig.getSignatureAlgorithm())
            .compact();
    }
    
    return accessToken;
}
```

As you can see we set Subject as the name of the authenticated user, which tells to whom the token is issued to. The token also has information on when it was issued and it's expiry time. We also used random UUID again, but here it is to uniquely identify the token in order to prevent token replay attacks. We have added the list of authorities of the authenticated user as custom claims using `addClaim()`. Finally we signed the token using the `SecretKey` and `SignatureAlgorithm` from `JwtConfig`.

We are no more required to store the token and it's expiry time in the `AppUser` record in the database. JWT is now self-contained to identify who the user is and what are they allowed to do in a given timeframe.

> **NOTE**
> 
> We have changed the variable name from `token` to `accessToken` in both `AuthenticationService` and `AuthenticationController`, as it is no more a simple opaque token. This new variable name better reflects its purpose that it now contains both authentication and authorization information necessary enough to access the resources in our application.

Let's tidy up the code a little by removing the `updateToken()` in `UserService` and it's pre-authorize condition from `SecurityConstants`. Restart the application and send a POST request to GenerateToken API and see the JWT generated as below.

[IMAGE]

We can now go to JWT's [official page](https://jwt.io/) to unpack the token and see what it contains. Before pasting the generated JWT, check the Checkbox with label *secret base64 encoded* in *Verify Signature* section, and replace the string *your-256-bit-secret* in the text box with the Base64 encoded `SecretKey` string from the properties. Now paste the generated JWT on the left hand side to see the Header and Payload details of the token on the right hand side.

[IMAGE]

We can also use JJWT library to parse the content of JWT using `JwtParserBuilder` from `Jwts` class as we will see in the next chapter while verifying the JWT.