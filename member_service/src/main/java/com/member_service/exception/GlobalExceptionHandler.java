package com.member_service.exception;


import com.member_service.payload.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // This method catches ONLY ResourceNotFoundExceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest) {

        // 1. Create our custom error payload
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(), // This grabs the "Member not found with ID: 3" message
                webRequest.getDescription(false) // This shows the API path (/api/v1/member/3)
        );

        // 2. Return it with a beautiful 404 NOT FOUND status
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Bonus: A fallback for any other random crashes (like database connection failures)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception exception,
            WebRequest webRequest) {

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException exception,
            WebRequest webRequest) {

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );
        // 409 CONFLICT is the industry standard for duplicate database entries
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // 2. Handle @Valid Validation Failures (Like blank names or bad emails)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Loop through all the validation errors and put them in a Map (Field Name -> Error Message)
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 400 BAD REQUEST is the standard for bad user input
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest webRequest) {

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(), // Will say "Access Denied"
                webRequest.getDescription(false)
        );

        // Return the correct 403 Forbidden status!
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
}