package com.sl.shortlink.controller;

import com.sl.shortlink.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaveFailException.class)
    public ResponseEntity<String> handleSaveFailException(SaveFailException ex) {
        log.warn("Failed to save ulr ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to save ulr");
    }

    @ExceptionHandler(InvalidShortCodeException.class)
    public ResponseEntity<String> handleInvalidShortCodeException(InvalidShortCodeException ex) {
        log.warn("Invalid short code ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Custom code must be 4-20 characters long and contain only letters, digits, '-' or '_'");
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.warn("URL not found based on code ", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("URL not found based on code");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User mot found, or not exist ", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User mot found, or not exist");
    }

    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<String> handleShortCodeAlreadyExistsException(ShortCodeAlreadyExistsException ex) {
        log.warn("Short code already in use ", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Short code already in use");
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        log.warn("Short code already in use ", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Username already exist");
    }

}
