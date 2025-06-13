package com.kimje.chat.user.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
	private final String message;
	public DuplicateResourceException(String message) {
		this.message = message;
	}
}
