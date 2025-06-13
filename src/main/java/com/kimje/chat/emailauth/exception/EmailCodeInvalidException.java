package com.kimje.chat.emailauth.exception;

import lombok.Getter;

@Getter
public class EmailCodeInvalidException extends RuntimeException {

	private final String message;

	public EmailCodeInvalidException(String message) {
		this.message = message;
	}
}
