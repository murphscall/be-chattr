package com.kimje.chat.global.exception.customexception;

import lombok.Getter;

@Getter
public class VerificationCodeExpiredException extends RuntimeException {

	private final String message;

	public VerificationCodeExpiredException(String message) {
		this.message = message;
	}

}
