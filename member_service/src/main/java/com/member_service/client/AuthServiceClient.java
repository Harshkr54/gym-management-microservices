package com.member_service.client;

import com.member_service.dto.AuthRegistrationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// The "name" must perfectly match the spring.application.name of your Auth Service in Eureka
@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {

    // This must match the exact URL path of the Auth Service's register endpoint
    @PostMapping("/api/v1/auth/register")
    ResponseEntity<Object> registerCredentials(@RequestBody AuthRegistrationDto request);
}