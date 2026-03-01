package com.taskManagementSystem.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request){
        ExceptionResponse response =
                new ExceptionResponse.Builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND)
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        ExceptionResponse response =
                new ExceptionResponse.Builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
