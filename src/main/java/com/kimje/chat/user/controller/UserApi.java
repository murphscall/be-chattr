package com.kimje.chat.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.dto.UserRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "사용자 및 인증 API")
public interface UserApi {
	@Operation(summary = "회원가입 요청", description = "회원가입을 요청합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원가입 완료"),
		@ApiResponse(responseCode = "401", description = "중복 및 에러")
	})
	ResponseEntity<?> create(
		@Valid @RequestBody UserRequestDTO.Create dto,
		BindingResult result);

	@Operation(summary = "회원 탈퇴 요청", description = "회원 탈퇴를 요청합니다.")
	ResponseEntity<?> delete(
		@RequestBody UserRequestDTO.Delete dto,
		@AuthenticationPrincipal AuthUser authUser);

	@Operation(summary = "회원 정보 요청", description = "회원 정보를 요청합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 정보"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
	})
	ResponseEntity<?> me(@AuthenticationPrincipal AuthUser authUser);
}
