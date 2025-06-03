package com.kimje.chat.chats.service.message;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageQueryService {

	private final ChatUserRepository chatUserRepository;
	private final MessageRepository messageRepository;

	@Transactional(readOnly = true)
	public List<MessageResponseDTO> getVisibleMessages(Long chatId, AuthUser authUser) {
		ChatUser chatUser = chatUserRepository
			.findByChatIdAndUserId(chatId, authUser.getUserId())
			.orElseThrow(() -> new IllegalStateException("채팅방에 참여한 이력이 없습니다."));

		LocalDateTime joinedAt = chatUser.getJoinedAt();

		return messageRepository.findByChatId_IdAndCreatedAtAfter(chatId, joinedAt)
			.stream().map(MessageResponseDTO::from).toList();

	}
}
