package com.fastcampus.healthcare.common.exception;

public class ForbiddenAccessException extends RuntimeException{
  public ForbiddenAccessException(String message) {
    super(message);
  }
}
