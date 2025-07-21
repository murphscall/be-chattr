package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatResponseDTO.ChatUserInfo;
import com.kimje.chat.chats.service.ChatCommandService;
import com.kimje.chat.chats.service.ChatQueryService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class ChatUserController {

	private final ChatCommandService chatCommandService;
	private final ChatQueryService chatQueryService;

	public ChatUserController(ChatCommandService chatCommandService, ChatQueryService chatQueryService) {
		this.chatCommandService = chatCommandService;
		this.chatQueryService = chatQueryService;
	}

	// 채팅방 입장
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats/{chatId}/join")
	public ResponseEntity<?> joinChat(@PathVariable("chatId") Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		chatCommandService.joinUser(chatId, authUser.getUserId());
		return ResponseEntity.ok().body(ApiResponse.success(Map.of("chatId", chatId)));
	}

	// 채팅방 나가기
	@PostMapping("/api/chats/{chatId}/exit")
	public ResponseEntity<?> exitChat(@PathVariable("chatId") Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		chatCommandService.exitUser(chatId, authUser.getUserId());
		return ResponseEntity.ok().body(ApiResponse.success(Map.of("chatId", chatId)));
	}



	// 채팅방 유저 목록
	@GetMapping("/api/chats/{chatId}/members")
	public ResponseEntity<?> getChatMembers(@PathVariable("chatId") Long chatId) {
		List<ChatUserInfo> chatUsers = chatQueryService.getMembers(chatId);
		return ResponseEntity.ok().body(ApiResponse.success(chatUsers));
	}

}
