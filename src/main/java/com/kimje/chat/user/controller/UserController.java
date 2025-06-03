package com.kimje.chat.user.controller;

import com.kimje.chat.global.exception.customexception.FieldErrorException;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.util.FieldErrorsHandlerUtil;
import com.kimje.chat.user.dto.UserRequestDTO;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "USER API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
public class UserController {
	private final FieldErrorsHandlerUtil fieldErrorsHandlerUtil;
	private final UserService userService;

	@PostMapping("/api/users")
	public ResponseEntity<?> create(@Valid @RequestBody UserRequestDTO.Create dto, BindingResult result) {
		if (result.hasErrors()) {
			fieldErrorsHandlerUtil.fieldErrorsHandler(result);
		}
		userService.createUser(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원 생성 완료"));
	}

	@DeleteMapping("/api/users")
	public ResponseEntity<?> delete(@RequestBody UserRequestDTO.Delete dto,
		@AuthenticationPrincipal AuthUser authUser) {
		long userId = authUser.getUserId();
		userService.deleteUser(dto, userId);
		return ResponseEntity.ok().body(ApiResponse.success("회원 탈퇴 완료되었습니다."));
	}

	@GetMapping("/api/users/me")
	public ResponseEntity<?> me(@AuthenticationPrincipal AuthUser authUser) {
		UserResponseDTO.Info userInfo = userService.getUserInfo(authUser.getUserId());

		return ResponseEntity.ok().body(ApiResponse.success(userInfo));
	}
}
