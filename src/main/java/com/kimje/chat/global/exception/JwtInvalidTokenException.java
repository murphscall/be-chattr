package com.kimje.chat.global.exception;

public class JwtInvalidTokenException extends RuntimeException {

	public JwtInvalidTokenException(String message) {
		super(message);
	}
}
