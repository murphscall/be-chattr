package com.kimje.chat.chats.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.kimje.chat.chats.dto.MessageRequestDTO;
import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.service.message.MessageCommandService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompController {
	private final MessageCommandService messageCommandService;
	private final SimpMessagingTemplate messagingTemplate;


	@MessageMapping("/api/chats/messages/send")
	public void sendMessage(@Payload MessageRequestDTO messageDto,
		StompHeaderAccessor headerAccessor) {

		Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
		if (userId == null) {
			// 예외 처리 필요
			return;
		}

		// 2. 실제 메시지 저장 로직은 Command 서비스에 위임
		Message savedMessage = messageCommandService.saveUserMessage(
			messageDto.getChatId(),
			userId,
			messageDto.getContent(),
			messageDto.getType()
		);

		// 3. DTO로 변환하여 클라이언트에 메시지 전송 (컨트롤러의 책임)
		MessageResponseDTO responseDto = MessageResponseDTO.of(savedMessage);
		String destination = "/sub/chats/" + messageDto.getChatId();
		messagingTemplate.convertAndSend(destination, responseDto);

	}
}
