package com.workout_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MEMBER-SERVICE")
public interface MemberServiceClient {

    @GetMapping("/api/v1/member/check-profile")
    boolean checkProfileExists(@RequestParam("email") String email);
}
