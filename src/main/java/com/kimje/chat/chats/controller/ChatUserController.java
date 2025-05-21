package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatResponseDTO.ChatInfo;
import com.kimje.chat.chats.service.ChatUserService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.dto.UserResponseDTO.Info;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatUserController {

	private ChatUserService chatUserService;

	// 채팅방 입장
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats/{chatId}/join")
	public ResponseEntity<?> joinChat(@PathVariable Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		Long responseChatId = chatUserService.joinUser(chatId, authUser.getUserId());
		return ResponseEntity.ok().body(ApiResponse.success(Map.of("chatId", responseChatId)));
	}

	// 채팅방 나가기
	@DeleteMapping("/api/chats/{chatId}/exit")
	public ResponseEntity<?> exitChat(@PathVariable Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		chatUserService.exitUser(chatId, authUser.getUserId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(Map.of("chatId", chatId)));
	}

	// 채팅방 유저 목록
	@GetMapping("/api/chats/{chatId}/members")
	public ResponseEntity<?> getChatMembers(@PathVariable Long chatId) {
		List<ChatInfo> chatUsers = chatUserService.getMembers(chatId);
		return ResponseEntity.ok().body(ApiResponse.success(chatUsers));
	}

}
