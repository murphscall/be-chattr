package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.service.ChatAdminService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="채팅 관리 API")
@RestController
public class ChatAdminController {

	private final ChatAdminService chatAdminService;

	public ChatAdminController(ChatAdminService chatAdminService) {
		this.chatAdminService = chatAdminService;
	}

	// 유저 추방
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats/{chatId}/users/{targetId}/kick")
	public ResponseEntity<ApiResponse<?>> kickChat(@PathVariable("chatId") Long chatId,
		@PathVariable("targetId") Long targetId , @AuthenticationPrincipal AuthUser authUser) {
		chatAdminService.kickUser(chatId , targetId , authUser);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	//채팅 방 내 권한 변경 (방장 권한)
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats/{chatId}/users/{targetId}/role")
	public ResponseEntity<?> promoteChat(@PathVariable Long chatId,
		@PathVariable Long targetId,
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ChatRequestDTO.ChangeRole dto) {
		chatAdminService.changeRole(authUser.getUserId(), chatId, targetId, dto);
		return ResponseEntity.ok().build();
	}

}
