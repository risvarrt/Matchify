package com.matchify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageTypeNotValidException extends RuntimeException{
    public ImageTypeNotValidException(String errorMessage){ super(errorMessage);}
}
