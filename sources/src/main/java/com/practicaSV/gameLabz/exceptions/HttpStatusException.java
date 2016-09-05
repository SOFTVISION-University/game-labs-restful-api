package com.practicaSV.gameLabz.exceptions;

import org.springframework.http.HttpStatus;

public class HttpStatusException extends RuntimeException{

    private HttpStatus httpStatus;

    public HttpStatusException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
