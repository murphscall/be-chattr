package com.kimje.chat.global.exception.customexception;

import lombok.Getter;

@Getter
public class EmailCodeInvalidException extends RuntimeException {

	private final String message;

	public EmailCodeInvalidException(String message) {
		this.message = message;
	}
}
