package com.kimje.chat.global.exception;

public class JwtTokenExpiredException extends RuntimeException {

  public JwtTokenExpiredException(String message) {
    super(message);
  }
}
