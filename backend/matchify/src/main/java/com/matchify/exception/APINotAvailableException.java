package com.matchify.exception;

public class APINotAvailableException extends RuntimeException{

    public APINotAvailableException(String message) {
        super(message);
    }
}
