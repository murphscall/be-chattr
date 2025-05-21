package com.kimje.chat.global.exception.customexception;

import java.util.Map;

import lombok.Getter;

@Getter
public class FieldErrorException extends RuntimeException {

	private final Map<String, String> errors;

	public FieldErrorException(Map<String, String> errors) {
		this.errors = errors;
	}

}
