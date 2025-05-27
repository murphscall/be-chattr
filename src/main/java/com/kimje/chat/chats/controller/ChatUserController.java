package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatResponseDTO.ChatUserInfo;
import com.kimje.chat.chats.service.ChatUserService;
import com.kimje.chat.chats.service.MessageService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import java.util.List;
import java.util.Map;



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

public class ChatUserController {

	private final ChatUserService chatUserService;
	private final MessageService messageService;

	public ChatUserController(ChatUserService chatUserService1, MessageService messageService) {
		this.chatUserService = chatUserService1;
		this.messageService = messageService;
	}

	// 채팅방 입장
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats/{chatId}/join")
	public ResponseEntity<?> joinChat(@PathVariable("chatId") Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		boolean exists = chatUserService.joinUser(chatId, authUser.getUserId());
		if(exists){
			messageService.sendJoinNotice(chatId,authUser);
		}
		return ResponseEntity.ok().body(ApiResponse.success(Map.of("chatId", chatId)));
	}

	// 채팅방 나가기
	@DeleteMapping("/api/chats/{chatId}/exit")
	public ResponseEntity<?> exitChat(@PathVariable("chatId") Long chatId, @AuthenticationPrincipal AuthUser authUser) {
		chatUserService.exitUser(chatId, authUser.getUserId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(Map.of("chatId", chatId)));
	}



	// 채팅방 유저 목록
	@GetMapping("/api/chats/{chatId}/members")
	public ResponseEntity<?> getChatMembers(@PathVariable("chatId") Long chatId) {
		List<ChatUserInfo> chatUsers = chatUserService.getMembers(chatId);
		return ResponseEntity.ok().body(ApiResponse.success(chatUsers));
	}

}
