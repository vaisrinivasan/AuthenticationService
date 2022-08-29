package com.org.authservice.exceptions;

public class DependencyException extends RuntimeException{

    public DependencyException(Throwable throwable) {
        super(throwable);
    }
    public DependencyException(String msg) {
        super(msg);
    }
}
