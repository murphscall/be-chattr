package com.kimje.chat.user.exception;

public class UserNotFoundException extends RuntimeException {
	public 	UserNotFoundException(String message , Long userId) {
		super(message);
	}
}
