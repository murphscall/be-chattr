package com.kimje.chat.chats.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.service.message.MessageCommandService;
import com.kimje.chat.chats.service.message.MessageQueryService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name="채팅 메시지 API")
@RestController
@Slf4j
public class MessageController {

	private final MessageCommandService messageCommandService;
	private final MessageQueryService messageQueryService;

	public MessageController(MessageCommandService messageCommandService , MessageQueryService messageQueryService) {
		this.messageCommandService = messageCommandService;
		this.messageQueryService = messageQueryService;
	}

	@GetMapping("/api/chats/{chatId}/message")
	public ResponseEntity<?> getMessages(@PathVariable("chatId") Long chatId , @AuthenticationPrincipal AuthUser authUser) {
		log.info("🟢[MESSAGE] 메시지 목록 요청 : email = {} , chatId = {}", authUser.getEmail(),chatId );
		List<MessageResponseDTO> messages = messageQueryService.getVisibleMessages(chatId,authUser);
		if(!messages.isEmpty()){
			log.info("🟢[MESSAGE] 메시지 목록 반환 : messages ={}", messages.get(0));
		}else{
			log.info("🟢[MESSAGE] 반환된 메시지 없음 : chatId = {}", chatId);
		}

		return ResponseEntity.ok().body(ApiResponse.success(messages));
	}

	@PostMapping("/api/chats/{chatId}/msg/{msgId}/likes")
	public ResponseEntity<ApiResponse<?>> likeMessage(@PathVariable("chatId") Long chatId, @PathVariable("msgId") Long msgId , @AuthenticationPrincipal AuthUser authUser) {
		messageCommandService.toggleLike(chatId, msgId , authUser.getUserId());
		return ResponseEntity.ok().body(ApiResponse.success("좋아요 토글 완료"));
	}

}
