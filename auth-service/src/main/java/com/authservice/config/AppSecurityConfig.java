package com.authservice.config;

import com.authservice.service.CustomerUserDetailsService;
import com.authservice.service.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private JwtFilter filter;

    String[] publicEndpoints = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/update-password",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**",
            "/eureka/**"
    };

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//
//        // 1. Pass in the service that talks to your MySQL database
//        daoAuthenticationProvider.setUserDetailsService(customerUserDetailsService);
//
//        // 2. Pass in your BCrypt password encoder
//        daoAuthenticationProvider.setPasswordEncoder(getEncoder());
//
//        // 3. Return the fully configured provider back to Spring
//        return daoAuthenticationProvider;
//    }

    @Bean
    public SecurityFilterChain securityConfig(HttpSecurity http) throws Exception{

        http
                .csrf(csrf -> csrf.disable()) // MUST disable CSRF for REST APIs
                .authorizeHttpRequests( req -> {
                    req.requestMatchers(publicEndpoints).permitAll()
                            // Only allow users with ADMIN or USER roles to access this
                            .requestMatchers(publicEndpoints).hasAnyRole("ADMIN","USER")
                            // All other requests require a valid token
                            .anyRequest().authenticated();
                })
                // CRUCIAL: Put the JwtFilter in front of the default Spring Security filter
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}