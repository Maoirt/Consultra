package com.example.auth_service.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final HttpStatus code;

    public UserException(String message, HttpStatus code) {

        super(message);
        this.code = code;
    }

    public HttpStatus getCode() {
        return code;
    }
}
