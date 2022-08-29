package com.org.authservice.exceptions;

public class InvalidInputException extends RuntimeException{

    public InvalidInputException(Throwable throwable) {
        super(throwable);
    }

    public InvalidInputException(String msg) {
        super(msg);
    }
}
