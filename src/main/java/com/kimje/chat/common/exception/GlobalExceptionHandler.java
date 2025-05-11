package com.kimje.chat.common.exception;

import com.kimje.chat.common.response.ApiResponse;
import com.kimje.chat.emailauth.controller.EmailController;
import com.kimje.chat.user.controller.UserController;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 필드 에러 예외 핸들러

  @ExceptionHandler(FieldErrorException.class)
  public ResponseEntity<ApiResponse<?>> handleFieldErrorsException(FieldErrorException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(e.getErrors()));
  }
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<ApiResponse<?>> handleMessagingException(MessagingException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler(InvalidVerificationCodeException.class)
  public ResponseEntity<ApiResponse<?>> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }
  @ExceptionHandler(VerificationCodeExpiredException.class)
  public ResponseEntity<ApiResponse<?>> handleVerificationCodeExpiredException(VerificationCodeExpiredException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }
  @ExceptionHandler(EmailNotVerificationException.class)
  public ResponseEntity<ApiResponse<?>> handleEmailNotVerificationException(EmailNotVerificationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
  }

}
