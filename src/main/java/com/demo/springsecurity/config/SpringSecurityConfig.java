package com.demo.springsecurity.config;

import com.demo.springsecurity.filter.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;


    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain get(HttpSecurity httpSecurity) throws Exception {

        // Disable CSRF token validation
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(customizer -> customizer
                // Permit endpoints without authentication
                .requestMatchers("h2-console/**", "login").permitAll()
                // Enable authentication for rest endpoints
                .anyRequest().authenticated());

        // To show the login form for authentication
//        httpSecurity.formLogin(Customizer.withDefaults());

        // To enable http client authentication by passing basic auth details
        httpSecurity.httpBasic(Customizer.withDefaults());

        // Make API as state less, In this case we will receive the new session ID at every request
        // So in this case we should remove the login form because every time we will log-in it will change the session ID and again the login form will be visible
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter just before the username password authentication filter
        // It will allow use to validate the request based on the bearer token passed in request
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Build custom SecurityFilterChain with the above configurations
        return httpSecurity.build();

//        return httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService getUserDetailsService() {
//
//        List<UserDetails> users = List
//                .of(User.withDefaultPasswordEncoder()
//                                .username("user1")
//                                .password("password1")
//                                .build(),
//                        User.withDefaultPasswordEncoder()
//                                .username("user2")
//                                .password("password2")
//                                .build());
//
//        return new InMemoryUserDetailsManager(users);
//    }
}
