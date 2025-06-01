package com.kimje.chat.global.exception.customexception;

import lombok.Getter;

@Getter
public class EmailCodeExpiredException extends RuntimeException {

	private final String message;

	public EmailCodeExpiredException(String message) {
		this.message = message;
	}

}
