# Spring Security

Tip: We can find the OWSAP security finding and security issues can be found in your application https://owasp.org/www-project-top-ten/


# Adding spring security dependency
	- implementation 'org.springframework.boot:spring-boot-starter-security'

	Note: By this line your all will be secured by the default security
		You will see a login form when you will try to call any API
		The default username is user and the password is printing in the startup logs of the application
		- This also maintain the session and have logout endpoint at /logout
	
	How there a login form without a code or any filter?
	Note: Spring boot security has many (> 11) filters by default. And the default login for handing was generated from that.


# Everything Servlet
	- We can use the jakarta.servlet.http.HttpServletRequest and jakarta.servlet.http.HttpServletResponse to play with the request and response 		Ex. serveletRequest.getSession().getId();
	- You will see that at every login spring security is setting a new session ID in the cookies


# Change default username or password
	- spring.security.user.name=<>
	- spring.security.user.password=<>
	- You will see if you are overriding the password then you will not see any random password in the startup logs
	Note: By this you are only overriding the default username and password but not for different users
		- From postman you can use the basic auth setting for request to get success


# CSRF handling
	- Suppose you are hitting any POST/PUT/DELET to your application  401😳 instead of 404/405 when the spring security dependency is in place because the CSRF token is not in your request.
	- In this case you need to have the CSRF token that you can send from your application in the previous request using the HttpServletRequest and send that token in header ‘X-CSRF-TOKEN’ of the next request.
		Ex: return (org.springframework.security.web.csrf.CsrfToken) request.getAttribute("_csrf");


# Custom security configuration
	- Create a configuration class with annotations @Configuration and @EnableWebSecurity
		This will be used to override the default configuration and use the custom security configuration
	- Override the default Security filters chain
		@Bean
		public SecurityFilterChain get(HttpSecurity httpSecurity) throws Exception {
			return httpSecurity.build();
		}
	- Default security has been overridden and you will not see any login form of 401 provided by the default configuration.
	- Authenticate in the custom httpSecurity => httpSecurity.authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated());
	- Enable login form => httpSecurity.formLogin(Customizer.withDefaults());
		But in this we will receive that login form for postman too, even when passing auth basic info
	- Run with basic auth instead of login page => httpSecurity.httpBasic(Customizer.withDefaults());


# UserDetailsService
	- Core interface which loads user-specific data.
	- Create simple bean
		@Bean
		public UserDetailsService getUserDetailsService() {
			return new InMemoryUserDetailsManager();
		}
	- Now the API will return 401 as it will not read the default application user provided by the spring security
	- It has multiple constructors accept Single/List of UserDetails
	- Now we can access our API with different user and roles


# AuthenticationProvider
	- This will help to get the users from the DB and do authentication on the database user
	- Not this provider is not only limited to DB, so for the database we have to get instance from DaoAuthenticationProvider
	- This will require the UserDetailsService object, so we need to create the custom implementation of this and override the loadUserByUsername(String username)
	- The loadUserByUsername return UserDetails object, either we can use the User class or implement the custom user principle
	- Here we can get the user from db using username and return UserDetails object rest spring security will handle the comparison and exception handing for the authenticated user
	- We should use a strong password encoder for storing our users and the same in the DaoAuthenticationProvider


# JWT [JSON Web Token]
	- Dependencies:
			implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
			runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
			runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
	- Now we will use the AuthenticationManage which behind the seen will use the AuthenticationProvider
	- Have 3 components Header,Payload,Signature
	- We can see the details of the token at JWT.io

	# Generate Token [Login API]
		- We can generate token using JWTs with subject, issue/expiry date and sing key
		- Add Bean of AuthenticationManager in the Security config
				@Bean
				public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
					return authConfig.getAuthenticationManager();
				}
		- Exclude the login and other non-auth API to  requestMatchers and permit them without authentication
		- Authenticate request: authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		- If authenticate.isAuthenticated() is true then generate and return a token

	# Validate Token [Follow up requests after login]
		- We need to define the JWT filters and override the doFilterInternal of OncePerRequestFilter to validate the each request those are not whitelisted
		- So the UsernamePasswordAuthenticationToken will be behind the JWT filter
			Register filter to SecurityConfig: httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		- get the username from the Bearer token and validate the token and user
		- Set valid user details to the security context 🎉
