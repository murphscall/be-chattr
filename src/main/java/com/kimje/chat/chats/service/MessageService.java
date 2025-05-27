package com.kimje.chat.chats.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
	private final MessageRepository messageRepo;
	private final SimpMessagingTemplate template;
	private final EntityManager em;
	private final ChatUserRepository chatUserRepository;
	private final UserRepository userRepository;

	public void sendJoinNotice(Long chatId, AuthUser authUser) {

		chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId());
		Chat chatRef = em.getReference(Chat.class, chatId);

		Message msg = Message.builder()
			.chatId(chatRef)              // 연관만 설정
			.senderUserId(null)                  // 시스템 메시지
			.content(authUser.getNickname() + "님이 입장했습니다!")
			.type(MessageType.NOTICE_JOIN)
			.createdAt(LocalDateTime.now())
			.isDeleted(false)
			.build();

		messageRepo.save(msg);                   // DB 저장

		template.convertAndSend(                 // 모든 참여자에게 전송
			"/sub/chats/" + chatId,
			MessageResponseDTO.of(msg)
		);
	}

	public void sendKickNotice(Long chatId, Long targetUserId) {
		Chat chatRef = em.getReference(Chat.class, chatId);
		User targetUser = userRepository.findById(targetUserId)
				.orElseThrow(() -> new UsernameNotFoundException("강퇴 대상이 존재하지 않습니다."));


		Message msg = Message.builder()
				.chatId(chatRef)
				.senderUserId(null)
				.content(targetUser.getName() + "님이 강퇴되었습니다.")
				.type(MessageType.NOTICE_KICK)
				.createdAt(LocalDateTime.now())
				.isDeleted(false)
				.build();

		messageRepo.save(msg);

		// 1. 전체 알림
		template.convertAndSend(
				"/sub/chats/" + chatId,
				MessageResponseDTO.of(msg, targetUserId)
		);


	}
}