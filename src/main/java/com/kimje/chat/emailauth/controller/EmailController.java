package com.kimje.chat.emailauth.controller;

import java.util.HashMap;
import java.util.Map;

import com.kimje.chat.global.exception.FieldErrorException;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.emailauth.dto.EmailRequestDTO;
import com.kimje.chat.emailauth.service.EmailService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="사용자 및 인증 API")
@RestController
public class EmailController {

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping("/api/email/send")
	public ResponseEntity<ApiResponse<?>> sendEmail(@Valid @RequestBody EmailRequestDTO.Send dto , BindingResult bindingResult ) throws MessagingException {
		if(bindingResult.hasErrors()){
			fieldErrorsHandler(bindingResult);
		}

		emailService.sendEmail(dto);
		return ResponseEntity.ok().body(ApiResponse.success("인증 코드가 전송 되었습니다."));
	}

	@PostMapping("/api/email/verify")
	public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestBody EmailRequestDTO.Verify dto) throws
		MessagingException {

		emailService.verifyCode(dto);
		return ResponseEntity.ok().body(ApiResponse.success("이메일 인증 완료"));
	}

	public void fieldErrorsHandler(BindingResult result) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError error : result.getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		throw new FieldErrorException(errors);
	}

}
