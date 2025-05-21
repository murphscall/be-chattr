package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.service.ChatService;
import com.kimje.chat.global.exception.customexception.FieldErrorException;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	// 전체 채팅방 목록
	@GetMapping("/api/chats/list")
	public String getChatsList() {
		return "getChats";
	}

	// 참여중인 채팅방
	@GetMapping("/api/chats/me")
	public String getMyChats() {
		return "getChats";
	}

	// 채팅방 생성
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats")
	public ResponseEntity<?> createChat(@Valid @RequestBody ChatRequestDTO.Create dto,
		@AuthenticationPrincipal AuthUser authUser,
		BindingResult result) {

		if (result.hasErrors()) {
			fieldErrorsHandler(result);
		}
		Long chatId = chatService.createChatRoom(authUser.getUserId(), dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(Map.of("chatId", chatId)));
	}

	// 채팅방 삭제
	@DeleteMapping("/api/chats/{chatId}")
	public String deleteChat(@PathVariable Long chatId) {

		return "deleteChats";
	}

	public void fieldErrorsHandler(BindingResult result) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError error : result.getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		throw new FieldErrorException(errors);
	}
}
