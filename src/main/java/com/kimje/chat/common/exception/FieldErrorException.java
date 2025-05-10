package com.kimje.chat.common.exception;

import java.util.Map;

public class FieldErrorException extends RuntimeException {

  private final Map<String, String> errors;

  public FieldErrorException(Map<String , String> errors) {
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

}
