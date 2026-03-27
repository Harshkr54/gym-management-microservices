package com.member_service.exception;

public class ResourceAlreadyExistsException extends RuntimeException{

    public ResourceAlreadyExistsException(String msg){
        super(msg);
    }
}
