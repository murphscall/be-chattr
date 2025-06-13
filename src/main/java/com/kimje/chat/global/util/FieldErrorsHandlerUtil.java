package com.kimje.chat.global.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.kimje.chat.global.exception.FieldErrorException;


@Component
public class FieldErrorsHandlerUtil {

	public void fieldErrorsHandler(BindingResult result) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError error : result.getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		throw new FieldErrorException(errors);
	}

}
