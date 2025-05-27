package com.kimje.chat.global.exception.exhandler.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kimje.chat.global.exception.customexception.EmailCodeExpiredException;
import com.kimje.chat.global.exception.customexception.EmailCodeInvalidException;
import com.kimje.chat.global.exception.customexception.EmailNotVerificationException;
import com.kimje.chat.global.exception.customexception.FieldErrorException;
import com.kimje.chat.global.exception.customexception.LoginFailException;
import com.kimje.chat.global.response.ApiResponse;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {
	private static final int STATUS_BAD_REQUEST = 400;
	private static final int STATUS_UNAUTHORIZED = 401;

	@ExceptionHandler(LoginFailException.class)
	public ResponseEntity<ApiResponse<?>> handleLoginFailException(LoginFailException e , HttpServletResponse response) {

		log.warn("🟡[API 요청 실패] 응답 코드 = {} | 이유 = {}", STATUS_BAD_REQUEST , "신원 불일치");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUsernameNotFoundException(UsernameNotFoundException e){
		log.warn("🟡[API 요청 실패] 응답코드 = {} | 이유 = {}" , STATUS_UNAUTHORIZED , " 존재하지 않는 사용자");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ApiResponse<?>> handleMessagingException(MessagingException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(EmailCodeInvalidException.class)
	public ResponseEntity<ApiResponse<?>> handleInvalidVerificationCodeException(EmailCodeInvalidException e) {
		log.warn("🟡[API 요청 실패] 이메일 코드 불일치 | 응답코드 = {}" , STATUS_BAD_REQUEST );
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(EmailCodeExpiredException.class)
	public ResponseEntity<ApiResponse<?>> handleVerificationCodeExpiredException(EmailCodeExpiredException e) {
		log.warn("🟡[API 요청 실패] 이메일 코드 만료 | 응답코드 = {}" , STATUS_BAD_REQUEST );
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException e) {
		log.info("🟡[Authentication] 인증 실패 : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
	}

	@ExceptionHandler(FieldErrorException.class)
	public ResponseEntity<ApiResponse<?>> handleFieldErrorsException(FieldErrorException e ) {
		log.warn("🟡[API 요청 실패] 유효성 검증 실패 | 응답코드 = {} " , STATUS_BAD_REQUEST);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(e.getErrors()));
	}
}
