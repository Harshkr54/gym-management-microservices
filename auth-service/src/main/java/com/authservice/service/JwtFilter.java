package com.authservice.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service // Tells Spring to manage this as a Bean so we can @Autowired it in the Config
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract the Authorization header from the incoming request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // 2. Check if the header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " to isolate the actual token
            try {
                // 3. Use your Auth0 JwtService to read the email from the token
                email = jwtService.validateTokenAndRetrieveSubject(token);
            } catch (Exception e) {
                System.out.println("Invalid JWT Token or Token Expired");
            }
        }

        // 4. If we found an email and the user isn't already authenticated in this thread...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Fetch the user from the database
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

            // 5. Create the Spring Security authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Officially log the user in for this specific request!
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 7. Continue the filter chain (let the request pass through to the Controller)
        filterChain.doFilter(request, response);
    }
}