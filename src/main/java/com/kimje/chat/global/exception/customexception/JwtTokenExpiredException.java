package com.kimje.chat.global.exception.customexception;

public class JwtTokenExpiredException extends RuntimeException {

	public JwtTokenExpiredException(String message) {
		super(message);
	}
}
