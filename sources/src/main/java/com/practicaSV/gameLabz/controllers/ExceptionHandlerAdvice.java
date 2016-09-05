package com.practicaSV.gameLabz.controllers;

import com.practicaSV.gameLabz.exceptions.HttpStatusException;
import com.practicaSV.gameLabz.utils.HttpHeadersConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
public class ExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity httpStatusHandler(HttpStatusException exception) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeadersConstants.ERROR_MESSAGE, exception.getMessage());

        logger.error(exception.getMessage(), exception);
        return new ResponseEntity(httpHeaders, exception.getHttpStatus());
    }

//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity authenticationHandler(AuthenticationException exception) {
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set(HttpHeadersConstants.ERROR_MESSAGE, "Username or password is wrong!");
//
//        return new ResponseEntity(httpHeaders, HttpStatus.UNAUTHORIZED);
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity jsonPayloadException(HttpMessageNotReadableException exception) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeadersConstants.ERROR_MESSAGE, "Invalid json payload!");

        logger.error(exception.getMessage(), exception);

        return new ResponseEntity(httpHeaders, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity genericExeptionHandler(Exception exception) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeadersConstants.ERROR_MESSAGE, "Exception occured!");

        logger.error(exception.getMessage(), exception);

        return new ResponseEntity(httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
