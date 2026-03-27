package com.member_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader("loggedInUserEmail");
        String role = request.getHeader("loggedInUserRole");

        // 1. ADD THESE PRINT STATEMENTS SO WE CAN SEE EXACTLY WHAT THE GATEWAY SENT
        System.out.println("--- SECURITY FILTER DEBUG ---");
        System.out.println("Email from Header: " + email);
        System.out.println("Role from Header: " + role);

        if (email != null && role != null) {
            // 2. CLEAN THE ROLE STRING: Remove any accidental quotes or brackets from the JWT parser
            String cleanRole = role.replace("\"", "").replace("[", "").replace("]", "").trim();
            System.out.println("Cleaned Role being applied: " + cleanRole);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(cleanRole);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email, null, Collections.singletonList(authority));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        System.out.println("-----------------------------");

        filterChain.doFilter(request, response);
    }
}