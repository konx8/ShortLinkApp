package com.sl.shortlink.controller;

import com.sl.shortlink.exception.InvalidShortCodeException;
import com.sl.shortlink.exception.SaveFailException;
import com.sl.shortlink.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaveFailException.class)
    public ResponseEntity<String> handleSaveFailException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to save ulr");
    }

    @ExceptionHandler(InvalidShortCodeException.class)
    public ResponseEntity<String> handleInvalidShortCodeException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Invalid short code");
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("URL not found based on code");
    }


}
