package com.org.authservice.exceptions;

public class DependencyException extends RuntimeException {

    public DependencyException(final Throwable throwable) {
        super(throwable);
    }

    public DependencyException(final String msg) {
        super(msg);
    }
}
