package com.kimje.chat.chats.service.message;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.chats.event.UserJoinedChatEvent;
import com.kimje.chat.chats.event.UserKickChatEvent;
import com.kimje.chat.chats.event.UserLeftChatEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemMessageEventListener {
	private final MessageCommandService messageCommandService;
	private final SimpMessagingTemplate messagingTemplate;

	@TransactionalEventListener // 트랜잭션 커밋 후 실행 보장
	public void handleUserJoinedEvent(UserJoinedChatEvent event) {
		String content = event.getUserName() + "님이 입장했습니다.";
		Message systemMessage = messageCommandService.saveSystemMessage(event.getChatId(), content, MessageType.NOTICE_JOIN);

		String destination = "/sub/chats/" + event.getChatId();
		messagingTemplate.convertAndSend(destination, MessageResponseDTO.of(systemMessage));
	}

	@TransactionalEventListener // 트랜잭션 커밋 후 실행 보장
	public void handleUserKickEvent(UserKickChatEvent event) {
		String content = event.getKickedUserName() + "님이 추방되었습니다.";
		Message systemMessage = messageCommandService.saveSystemMessage(event.getChatId(), content, MessageType.NOTICE_JOIN);

		String destination = "/sub/chats/" + event.getChatId();
		messagingTemplate.convertAndSend(destination, MessageResponseDTO.of(systemMessage));
	}

	@TransactionalEventListener // 트랜잭션 커밋 후 실행 보장
	public void handleUserJoinedEvent(UserLeftChatEvent event) {
		String content = event.getLeaverUserName() + "님이 퇴장했습니다.";
		Message systemMessage = messageCommandService.saveSystemMessage(event.getChatId(), content, MessageType.NOTICE_JOIN);

		String destination = "/sub/chats/" + event.getChatId();
		messagingTemplate.convertAndSend(destination, MessageResponseDTO.of(systemMessage));
	}
}
