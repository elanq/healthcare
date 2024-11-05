package com.fastcampus.healthcare.common.exception;

public class PaymentException extends RuntimeException {
  public PaymentException(String message) {
    super(message);
  }
}