package com.kimje.chat.chats.controller;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.kimje.chat.chats.dto.MessageRequestDTO;
import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.repository.ChatRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;
import com.kimje.chat.user.service.UserService;

@Controller
public class ChatMessageController {
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final UserRepository userRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatRepository chatRepository;
	private final MessageRepository messageRepository;


	public ChatMessageController(SimpMessagingTemplate simpMessagingTemplate,
		UserRepository userRepository,
		ChatUserRepository chatUserRepository,
		ChatRepository chatRepository, MessageRepository messageRepository) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.userRepository = userRepository;
		this.chatRepository = chatRepository;
		this.chatUserRepository = chatUserRepository;
		this.messageRepository = messageRepository;
	}

	@MessageMapping("/api/chats/messages/send")
	public void sendMessage(@Payload MessageRequestDTO message,
		StompHeaderAccessor headerAccessor) {

		// 1. 세션에서 userId 추출
		Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

		// 2. 유저 검증 및 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

		if (chatUserRepository.findByChatIdAndUserId(message.getChatId(), userId).isEmpty()) {
			throw new AccessDeniedException("이 채팅방에 참여 중인 사용자가 아닙니다.");
		}


		Chat chat = chatRepository.findById(message.getChatId())
			.orElseThrow(()-> new IllegalStateException("존재하지 않는 채팅방입니다."));
		Message saveMessage = new Message();
		saveMessage.setChatId(chat);
		saveMessage.setContent(message.getContent());
		saveMessage.setType(message.getType());
		saveMessage.setCreatedAt(LocalDateTime.now());
		saveMessage.setSenderUserId(user);
		saveMessage.setDeleted(false);

		messageRepository.save(saveMessage);

		// 3. 안전한 메시지 구성
		MessageResponseDTO safeMessage = new MessageResponseDTO();
		safeMessage.setId(saveMessage.getId());
		safeMessage.setChatId(message.getChatId());
		safeMessage.setContent(message.getContent());
		safeMessage.setType(message.getType());  // CHAT, ENTER 등
		safeMessage.setSenderId(user.getId());
		safeMessage.setSenderName(user.getName());
		safeMessage.setCreatedAt(saveMessage.getCreatedAt());
		safeMessage.setDeleted(false);

		// 4. 메시지 전송 (Redis Pub/Sub)
		String destination = "/sub/chats/" + message.getChatId();
		simpMessagingTemplate.convertAndSend(destination, safeMessage);

	}
}
