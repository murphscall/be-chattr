package com.kimje.chat.emailauth.exception;

import lombok.Getter;

@Getter
public class EmailCodeExpiredException extends RuntimeException {

	private final String message;

	public EmailCodeExpiredException(String message) {
		this.message = message;
	}

}
