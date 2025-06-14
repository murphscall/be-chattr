package com.kimje.chat.chats.controller;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.service.RoomQueryService;
import com.kimje.chat.chats.service.RoomCommandService;
import com.kimje.chat.chats.service.ChatUserService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.response.PageResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.util.FieldErrorsHandlerUtil;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomQueryService roomQueryService;
	private final RoomCommandService roomCommandService;
	private final ChatUserService chatUserService;
	private final FieldErrorsHandlerUtil fieldErrorsHandlerUtil;

	// 전체 채팅방 목록
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/api/chats/list")
	public ResponseEntity<ApiResponse<?>> getChatsList(Pageable pageable) {
		PageResponse<ChatResponseDTO.ChatInfo> chatList = roomQueryService.getChats(pageable);
		return ResponseEntity.ok().body(ApiResponse.success(chatList));
	}

	// 핫한 채팅방 목록
	@GetMapping("/api/chats/hot")
	public ResponseEntity<ApiResponse<?>> getHotChats(Pageable pageable) {
		PageResponse<ChatResponseDTO.ChatInfo> chatList = roomQueryService.getHotChats(pageable);
		return ResponseEntity.ok().body(ApiResponse.success(chatList));
	}

	// 참여중인 채팅방
	@GetMapping("/api/chats/me")
	public ResponseEntity<?> getMyChats(@AuthenticationPrincipal AuthUser authUser , Pageable pageable) {
		PageResponse<ChatResponseDTO.ChatInfo> myChats = roomQueryService.getMyChats(authUser,pageable);
		return ResponseEntity.ok().body(ApiResponse.success(myChats));
	}

	@GetMapping("/api/chats/my")
	public ResponseEntity<?> getCreateByMeChats(@AuthenticationPrincipal AuthUser authUser) {
		List<ChatResponseDTO.ChatInfo> list = chatUserService.getCreateByMeChats(authUser.getUserId());
		return ResponseEntity.ok().body(ApiResponse.success(list));
	}


	// 채팅방 생성
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/api/chats")
	public ResponseEntity<?> createChat(@Valid @RequestBody ChatRequestDTO.Create dto,
		@AuthenticationPrincipal AuthUser authUser,
		BindingResult result) {
		if (result.hasErrors()) {
			fieldErrorsHandlerUtil.fieldErrorsHandler(result);
		}
		ChatResponseDTO.ChatInfo chatResponse = roomCommandService.createChatRoom(authUser.getUserId(), dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(chatResponse));
	}

}
