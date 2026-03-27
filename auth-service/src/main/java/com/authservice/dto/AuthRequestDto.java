package com.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


public class AuthRequestDto {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Getter
    @Setter
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String role;

}