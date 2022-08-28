package com.org.authservice.exceptions;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException(String msg) {
        super(msg);
    }
}
