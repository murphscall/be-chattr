package com.kimje.chat.global.exception.customexception;

import lombok.Getter;

@Getter
public class EmailNotVerificationException extends RuntimeException {

	private final String message;

	public EmailNotVerificationException(String message) {
		this.message = message;
	}
}
