package com.kimje.chat.chats.service.message;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kimje.chat.chats.dto.MessageRequestDTO;
import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final UserRepository userRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatRoomRepository chatRepository;
	private final MessageRepository messageRepository;


	public void sendChatMessage(MessageRequestDTO message, StompHeaderAccessor headerAccessor) {
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
		Message saveMessage = message.toEntity(chat , user);
		messageRepository.save(saveMessage);

		// 3. 안전한 메시지 구성
		MessageResponseDTO safeMessage = MessageResponseDTO.of(saveMessage);

		// 4. 메시지 전송 (Redis Pub/Sub)
		String destination = "/sub/chats/" + message.getChatId();
		simpMessagingTemplate.convertAndSend(destination, safeMessage);
	}
}
