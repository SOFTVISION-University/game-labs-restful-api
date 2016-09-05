package com.practicaSV.gameLabz.exceptions;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends HttpStatusException {

    public AuthorizationException(HttpStatus httpStatus, String errorMessage) {
        super(httpStatus, errorMessage);
    }
}
