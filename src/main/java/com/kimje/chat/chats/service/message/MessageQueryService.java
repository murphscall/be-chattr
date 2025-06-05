package com.kimje.chat.chats.service.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.repository.MessageLikeRepository;
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
	private final MessageLikeRepository messageLikeRepository;

	@Transactional(readOnly = true)
	public List<MessageResponseDTO> getVisibleMessages(Long chatId, AuthUser authUser) {
		ChatUser chatUser = chatUserRepository
			.findByChatIdAndUserId(chatId, authUser.getUserId())
			.orElseThrow(() -> new IllegalStateException("채팅방에 참여한 이력이 없습니다."));

		LocalDateTime joinedAt = chatUser.getJoinedAt();
		List<Message> messages = messageRepository.findByChatId_IdAndCreatedAtAfter(chatId, joinedAt);

		Set<Long> likedMessageIds = messageLikeRepository.findLikedMessageIdsByUserIdAndChatIdAfter(
				authUser.getUserId(), chatId, joinedAt
		);

		return messages.stream()
				.map(msg -> {
					MessageResponseDTO dto = MessageResponseDTO.of(msg); // 기본 변환
					dto.setLikedByMe(likedMessageIds.contains(msg.getId())); // 좋아요 여부 추가
					dto.setLikeCount(msg.getLikes().size()); // 좋아요 수 추가
					return dto;
				})
				.toList();

	}
}
