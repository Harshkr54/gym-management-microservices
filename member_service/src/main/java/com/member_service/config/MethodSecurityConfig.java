package com.member_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // This enables the @PreAuthorize annotation
public class MethodSecurityConfig {
}
