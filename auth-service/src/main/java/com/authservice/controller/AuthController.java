package com.authservice.controller;

import com.authservice.dto.AuthRequestDto;
import com.authservice.response.ApiResponse;
import com.authservice.service.AuthService;
import com.authservice.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody AuthRequestDto authDto) {

        // 1. Process the registration in the service layer
        ApiResponse<String> response = authService.register(authDto);

        // If success is false, return 409 Conflict (best for "already exists")
        if (!response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // 2. Return the custom response wrapper with a 201 CREATED status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@RequestBody AuthRequestDto authDto) {
        try {
            // 1. Create the authentication token from request data
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    authDto.getEmail(), authDto.getPassword()
            );

            // 2. Authenticate the user (This calls your UserDetailsService)
            Authentication authentication = authenticationManager.authenticate(token);

            if (authentication.isAuthenticated()) {
                //Get the role from the authenticated user object
                String role = authentication.getAuthorities().iterator().next().getAuthority();

                // 3. Generate the JWT Token (Replace with your JwtService logic)
                String jwtToken = jwtService.generateToken(authDto.getEmail(),role);

                // 4. Return Success Response
                ApiResponse<String> response = new ApiResponse<>(
                        true,
                        "Login successful",
                        jwtToken
                );
                return ResponseEntity.ok(response);
            } else {
                throw new RuntimeException("Authentication failed");
            }

        } catch (AuthenticationException e) {
            // 5. Handle Bad Credentials or Locked Accounts
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    false,
                    "Invalid email or password",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


}
