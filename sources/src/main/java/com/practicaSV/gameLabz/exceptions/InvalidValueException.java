package com.practicaSV.gameLabz.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidValueException extends HttpStatusException {

    public InvalidValueException(HttpStatus httpStatus, String errorMessage) {
        super(httpStatus, errorMessage);
    }
}
