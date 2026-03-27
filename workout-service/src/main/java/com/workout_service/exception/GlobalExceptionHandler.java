package com.workout_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // This method intercepts ANY AccessDeniedException thrown by @PreAuthorize
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest request) {

        // Create a custom JSON response format
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", "Forbidden");

        // YOUR CUSTOM MESSAGE HERE:
        errorDetails.put("message", "Access Denied: You are a Member. Only a Trainer or Owner can create a workout plan.");

        // Grab the path they tried to hit (e.g., /api/v1/workouts/create)
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ProfileIncompleteException.class)
    public ResponseEntity<Map<String, Object>> handleProfileIncompleteException(
            ProfileIncompleteException exception,
            WebRequest request) {

        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", "Profile Incomplete");

        // This will grab your exact string: "Access Denied: You must complete your Member Profile..."
        errorDetails.put("message", exception.getMessage());

        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ServiceDownException.class)
    public ResponseEntity<Map<String, Object>> handleServiceDownException(
            ServiceDownException exception,
            WebRequest request) {

        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        // 503 is the official HTTP status for "Server is down for maintenance"
        errorDetails.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorDetails.put("error", "Service Unavailable");

        // This grabs your exact string: "The Member Service is currently undergoing maintenance..."
        errorDetails.put("message", exception.getMessage());

        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }
}