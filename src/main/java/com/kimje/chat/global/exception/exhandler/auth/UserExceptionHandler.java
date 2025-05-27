package com.kimje.chat.global.exception.exhandler.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kimje.chat.global.exception.customexception.DuplicateResourceException;
import com.kimje.chat.global.exception.customexception.UserNotFoundException;
import com.kimje.chat.global.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {


	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiResponse<?>> handleDuplicateResourceException(DuplicateResourceException e) {
		log.info("🟡[API 요청 실패] 중복된 리소스 ");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException e ,Long userId){
		log.error("🔴[REFRESH] 존재하지 않는 사용자 | userId={}", userId);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
	}

}
