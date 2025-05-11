package com.kimje.chat.common.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class VerificationCodeExpiredException extends RuntimeException {

  private final String message;

  public VerificationCodeExpiredException(String message) {
    this.message = message;
  }

}
