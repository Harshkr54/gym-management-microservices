package com.workout_service.service;

import com.workout_service.client.MemberServiceClient;
import com.workout_service.exception.ServiceDownException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class MemberProfileValidator {

    private final MemberServiceClient memberServiceClient;

    public MemberProfileValidator(MemberServiceClient memberServiceClient) {
        this.memberServiceClient = memberServiceClient;
    }

    // Now that this is in a separate class, the Spring Proxy will catch it!
    @CircuitBreaker(name = "memberService", fallbackMethod = "fallbackProfileCheck")
    public boolean verifyTrainerProfile(String email) {
        return memberServiceClient.checkProfileExists(email);
    }

    public boolean fallbackProfileCheck(String email, Throwable throwable) {
        System.out.println("CIRCUIT BREAKER TRIPPED! Error: " + throwable.getMessage());

        throw new ServiceDownException("The Member Service is currently undergoing maintenance. Please try again later.");
    }

}