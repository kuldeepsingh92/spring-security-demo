package com.demo.springsecurity.filter;

import com.demo.springsecurity.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZCIsImlhdCI6MTcyNzAwODk0MSwiZXhwIjoxNzI3MDA4OTc3fQ.gQD1oGSvNT_dOW207JTld8rL7EB60GKcJvKCCc6KDNA

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // Check if the token is already validated and is in the security context
        if (null == authHeader || null != SecurityContextHolder.getContext().getAuthentication()) {

            // call to the next filter
            filterChain.doFilter(request, response);

            // terminate
            return;
        }

        // Validate the auth header contains a bearer token
        if (!authHeader.startsWith("Bearer")) {
            throw new RuntimeException("Invalid authorization header");
        }

        // Get the token from the Bearer string
        String token = authHeader.substring(7);

        // Extract username from the token
        String username = jwtService.getUsername(token);

        if( null == username) {
            throw new RuntimeException("Username doesn't exists");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Validate the token
        if(jwtService.validateToken(token, userDetails)) {

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Add authenticationToken in the security context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // call to the next filter
        filterChain.doFilter(request, response);
    }
}
