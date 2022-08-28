package com.org.authservice.exceptions;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String msg) {
        super(msg);
    }
}
