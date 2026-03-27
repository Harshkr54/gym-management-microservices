package com.workout_service.exception;

public class ServiceDownException extends RuntimeException {
    public ServiceDownException(String message) {
        super(message);
    }
}