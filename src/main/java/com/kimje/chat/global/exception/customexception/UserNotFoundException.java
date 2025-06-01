package com.kimje.chat.global.exception.customexception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message , Long userId) {
		super(message);
	}
}
