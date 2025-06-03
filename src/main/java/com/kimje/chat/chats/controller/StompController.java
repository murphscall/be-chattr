package com.kimje.chat.chats.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.kimje.chat.chats.dto.MessageRequestDTO;
import com.kimje.chat.chats.service.message.ChatMessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompController {
	private final ChatMessageService chatMessageService;


	@MessageMapping("/api/chats/messages/send")
	public void sendMessage(@Payload MessageRequestDTO message,
		StompHeaderAccessor headerAccessor) {

		chatMessageService.sendChatMessage(message,headerAccessor);

	}
}
