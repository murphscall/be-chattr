package com.kimje.chat.global.exception.exhandler;

import com.kimje.chat.global.exception.customexception.FieldErrorException;
import com.kimje.chat.global.exception.customexception.EmailCodeInvalidException;
import com.kimje.chat.global.exception.customexception.EmailCodeExpiredException;
import com.kimje.chat.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// 필드 에러 예외 핸들러

	@ExceptionHandler(FieldErrorException.class)
	public ResponseEntity<ApiResponse<?>> handleFieldErrorsException(FieldErrorException e) {
		log.info("");
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(e.getErrors()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}






}
