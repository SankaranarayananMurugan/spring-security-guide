# JSON Web Token

Imagine you are going to your office located in a multi-storey building which hosts it's space for multiple companies like yours. You will be required to display your ID card to identify who you are inside the building premise. You will also be required to carry your ACCESS card to let you inside your office space only. Similarly we need a self-contained token to identify who you are and what are you allowed to do.

JSON Web Token carries these two details along with the few other key informations. And it addresses almost all the challenges we raised with opaque tokens in the previous chapter.

### What is JSON Web Token (JWT)?

JSON Web Token is an open standard that defines a compact and self-contained way for securely transmitting information between parties as a JSON object. This information can be verified and trusted because it is digitally signed.

As the official definition suggests JWT can be used to securely transmit any information, we can use it to carry the authentication and authorization details of the user or the Subject. Remember JWT is not an authentication mechanism, it is just a token specification which can be used as Token-based authentication as well as Token-based authorization.

A quick read on What, When, Why and How about [JSON Web Token](https://jwt.io/introduction) can be helpful before start using it in our course. As the official page presents the necessary details about JWT in a concise manner, I do not want to replicate the same content in different words.

### How do we use JWT?
We do not have to reinvent the wheel in order to generate and validate the JWTs. As we mentioned earlier, we will use battle-tested production grade open source libraries. The official page of JWT also lists some of the [libraries](https://jwt.io/libraries?language=Java) we can use. We will use [JJWT](https://github.com/jwtk/jjwt) library in our course to generate and validate JWTs.

Let's add below JJWT dependencies in our pom.xml, and the latest version as I write this is 0.11.5.

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>${jjwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>${jjwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jjwt.version}</version>
</dependency>
```

JJWT library provides a Builder pattern with convenient setter methods for standard registered Claim names defined in the JWT specification. They are:

| Setter | Claim Name | Purpose |
|--|--|--|
| setSubject | sub | Principal to whom the token issued to |
| setExpiration | exp | Time on or after which token is invalid |
| setIssuedAt | iat | Time on which token was issued |
| setId | jti | Unique identifier for the token |
| setIssuer | iss | Principal who issued the token |
| setAudience| aud | Recipients to whom the token is intended for
| setNotBefore| nbf | Time on or before which token is invalid |

None of the above claim names are mandatory to use, it provides a starting point for a set of useful, interoperable claims. Custom claims that doesn't match the above setter can be set using `addClaims()`. As mentioned earlier, in order to use JWT as token-based authorization we will add the list of authorities as custom claims. This will reduce the need to query the database to load the `UserDetails` on every request in `TokenVerificationFilter`.

Let's see how to generate JSON Web Token in the next chapter.