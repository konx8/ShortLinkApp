package com.sl.shortlink.exception;

public class SaveFailException extends RuntimeException {
  public SaveFailException(String message, Throwable cause) {
    super(message, cause);
  }

}
