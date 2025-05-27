package com.kimje.chat.global.exception.customexception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFailException extends RuntimeException {

	private final String message;

	public LoginFailException(String message) {
		this.message = message;
	}
}
