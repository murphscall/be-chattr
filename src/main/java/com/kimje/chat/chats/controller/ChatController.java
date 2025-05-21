package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.repository.ChatRepository;
import com.kimje.chat.chats.service.ChatService;
import com.kimje.chat.global.exception.customexception.FieldErrorException;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.response.PageResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;


	// 전체 채팅방 목록
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/api/chats/list")
	public ResponseEntity<ApiResponse<?>> getChatsList(Pageable pageable) {
		PageResponse<ChatResponseDTO.ChatInfo> chatList = chatService.getChats(pageable);
		return ResponseEntity.ok().body(ApiResponse.success(chatList));
	}

	// 참여중인 채팅방
	@GetMapping("/api/chats/me")
	public ResponseEntity<?> getMyChats(@AuthenticationPrincipal AuthUser authUser , Pageable pageable) {
		PageResponse<ChatResponseDTO.ChatInfo> myChats = chatService.getMyChats(authUser,pageable);
		return ResponseEntity.ok().body(ApiResponse.success(myChats));
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
		ChatResponseDTO.ChatInfo chatResponse = chatService.createChatRoom(authUser.getUserId(), dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(chatResponse));
	}

	@GetMapping("/api/chats/{chatId}/message")
	public ResponseEntity<?> getMessages(@PathVariable("chatId") Long chatId , @AuthenticationPrincipal AuthUser authUser) {
		log.info("[MESSAGE] 메시지 목록 요청 : email = {} , chatId = {}", authUser.getEmail(),chatId );
		List<MessageResponseDTO> messages = chatService.getVisibleMessages(chatId,authUser);
		log.info("[MESSAGE] 메시지 목록 반환 : messages ={}", messages);
		return ResponseEntity.ok().body(ApiResponse.success(messages));
	}

	public void fieldErrorsHandler(BindingResult result) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError error : result.getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		throw new FieldErrorException(errors);
	}
}
