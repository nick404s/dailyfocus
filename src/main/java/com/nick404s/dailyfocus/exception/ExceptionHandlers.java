package com.nick404s.dailyfocus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(ResponseStatusException.class) // custom response exception handling
    public ResponseEntity<ExceptionResponse> handleException(ResponseStatusException responseStatusException){
        return buildResponseEntity(
                responseStatusException,
                HttpStatus.valueOf(responseStatusException.getStatusCode().value())
        );
    }

    @ExceptionHandler(Exception.class) // global exception handling
    public ResponseEntity<ExceptionResponse> handleException(Exception exception){
        return buildResponseEntity(exception, HttpStatus.BAD_REQUEST); // if something unknown default to bad request
    }

    private ResponseEntity<ExceptionResponse> buildResponseEntity(Exception exception, HttpStatus httpStatus){
        ExceptionResponse error = new ExceptionResponse();
        // set the params
        error.setStatus(httpStatus.value());
        error.setMessage(exception.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, httpStatus);
    }
}
