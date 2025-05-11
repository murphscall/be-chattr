package com.kimje.chat.common.exception;

import lombok.Getter;

@Getter
public class InvalidVerificationCodeException extends RuntimeException {

  private final String message;

  public InvalidVerificationCodeException(String message) {
    this.message = message;
  }
}
