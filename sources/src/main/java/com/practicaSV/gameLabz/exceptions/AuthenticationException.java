package com.practicaSV.gameLabz.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends HttpStatusException {

    public AuthenticationException(HttpStatus httpStatus, String errorMessage) {
        super(httpStatus, errorMessage);
    }
}
