package com.workout_service.exception;

// We extend RuntimeException, but give it a specific name!
public class ProfileIncompleteException extends RuntimeException {
    public ProfileIncompleteException(String message) {
        super(message);
    }
}