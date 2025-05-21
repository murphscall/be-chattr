package com.kimje.chat.global.exception.customexception;

public class JwtInvalidTokenException extends RuntimeException {

	public JwtInvalidTokenException(String message) {
		super(message);
	}
}
