package com.sl.shortlink.exception;

public class InvalidShortCodeException extends RuntimeException {
    public InvalidShortCodeException(String message) {
        super(message);
    }
}
