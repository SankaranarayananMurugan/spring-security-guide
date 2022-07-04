
# Add Spring Security

Let’s start adding Spring Security to the application to secure all the REST APIs, which are now accessible to everyone. Add `spring-boot-starter-security` dependency to the `pom.xml` as below:
```
<dependency>  
	<groupId>org.springframework.boot</groupId>  
	<artifactId>spring-boot-starter-security</artifactId>  
</dependency>
```
Accessing the [List Courses](http://localhost:8080/api/v1/courses) API from Postman results in *401 Unauthorized* error.

![List courses API response - Postman](./assets/list_courses_postman.png)

Accessing the same API in browser redirects us to the below [Login](http://localhost:8080/login) page.

![Spring Security Login page - Postman](./assets/login_browser.png)

By adding Spring Security, the framework adds some default behaviours to the application, where all the resources (incl. static resources) are secured by default. It expects the user to authenticate himself with *username* and *password* to access these resources. It automatically creates an in-memory user named ***user***.

One can notice something similar to the below lines in the application startup log, where a random password is generated for this in-memory user.

```
Using generated security password: fdea9fea-3b53-43f8-b78b-046ca27b03af

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```

Logging in with the ***user*** and this ***random password*** in the browser authenticates the user successfully and responds with the list of courses.

![Spring Security Login page - Postman](./assets/login_with_credentials.png)

![Spring Security Login page - Postman](./assets/list_courses_browser.png)
