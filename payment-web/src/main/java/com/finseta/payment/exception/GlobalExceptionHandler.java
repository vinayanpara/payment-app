package com.finseta.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(WebExchangeBindException ex) {
        // Extract error messages from the exception
        List<ErrorResponse.ErrorDetail> errors = ex.getFieldErrors().stream()
                .map(error -> new ErrorResponse.ErrorDetail(error.getDefaultMessage()))
                .collect(Collectors.toList());

        // Create the error response
        ErrorResponse errorResponse = new ErrorResponse(errors);

        // Return the error response with HTTP status 400 (Bad Request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
