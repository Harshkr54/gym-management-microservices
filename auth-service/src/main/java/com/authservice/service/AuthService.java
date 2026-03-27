package com.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.authservice.dto.AuthRequestDto;
import com.authservice.entity.MemberCredential;
import com.authservice.repository.MemberCredentialRepository;
import com.authservice.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.authservice.service.JwtService.SECRET_KEY;

@Service // CRUCIAL: Tells Spring to manage this class
public class AuthService {

    @Autowired
    private MemberCredentialRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // Note: Validation (@Valid) happens in the Controller. You don't need it here.
    public ApiResponse<String> register(AuthRequestDto authDto) {
        // 1. Check if exists
        if (repository.existsByEmail(authDto.getEmail())) {
            return new ApiResponse<>(false, "Email is already registered.");
        }

        // 2. Save User
        MemberCredential credential = new MemberCredential();
        credential.setEmail(authDto.getEmail());
        credential.setPassword(passwordEncoder.encode(authDto.getPassword()));

        // CHANGE THIS: Use the role from the DTO, default to MEMBER if empty
        String roleToAssign = (authDto.getRole() != null) ? authDto.getRole() : "ROLE_MEMBER";
        credential.setRole(roleToAssign);

        repository.save(credential);

        // 3. GENERATE TOKEN (Add this part)
        // Assuming you have a jwtService or a method to generate tokens
        String token = jwtService.generateToken(credential.getEmail(),credential.getRole());

        // 4. Return the Token in the "data" field
        return new ApiResponse<>(true, "Member registered successfully", token);
    }

}