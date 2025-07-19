package com.kimje.chat.global.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtInvalidTokenException extends AuthenticationException {

	public JwtInvalidTokenException(String message) {
		super(message);
	}
}
