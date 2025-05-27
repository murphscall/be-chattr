package com.kimje.chat.chats.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.dto.MessageResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.global.response.PageResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

	private static final int MAX_OPEN_CHATS_PER_USER = 5;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;
	private final ChatUserRepository chatUserRepository;
	private final MessageRepository messageRepository;

	@Transactional
	public ChatResponseDTO.ChatInfo createChatRoom(Long userId, ChatRequestDTO.Create dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		validateOpenChatLimit(userId);

		// 3. Chat 엔티티 생성
		Chat chat = Chat.builder()
			.title(dto.getTitle())
			.description(dto.getDescription())
			.topic(dto.getTopic())
			.build();
		chatRepository.save(chat);

		// 4. 생성자를 참여자로 저장 (MASTER 역할)
		ChatUser chatUser = ChatUser.builder()
			.chat(chat)
			.user(user)
			.role(ChatRole.MASTER)
			.build();
		chatUserRepository.save(chatUser);

		// 5. 채팅방 ID 반환

		ChatResponseDTO.ChatInfo chatInfo = ChatResponseDTO.ChatInfo.from(chat);
		chatInfo.setCreatedAt(chat.getCreatedAt());
		chatInfo.setMemberCount(1L);

		return chatInfo;
	}

	public PageResponse<ChatResponseDTO.ChatInfo> getChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> chats = chatRepository.getChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(chats);
		return pageResponse;
	}

	public PageResponse<ChatResponseDTO.ChatInfo> getHotChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> hotChats = chatRepository.findHotChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(hotChats);
		return pageResponse;
	}

	public PageResponse<ChatResponseDTO.ChatInfo> getMyChats(AuthUser authUser ,Pageable pageable) {

		Page<ChatResponseDTO.ChatInfo> myChats = chatUserRepository.findMyChatsWithCount(authUser.getUserId(), pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(myChats);
		return pageResponse;
	}

	public void validateOpenChatLimit(Long userId) {
		int count = chatUserRepository.countByUserIdAndRole(userId, ChatRole.MASTER);

		if (count >= MAX_OPEN_CHATS_PER_USER) {
			throw new IllegalStateException("오픈 채팅방은 최대 5개까지만 생성할 수 있습니다. 추가로 생성을 원할 시에 기존의 방을 삭제해주세요");
		}
	}

	public List<MessageResponseDTO> getVisibleMessages(Long chatId, AuthUser authUser) {
		ChatUser chatUser = chatUserRepository
			.findByChatIdAndUserId(chatId, authUser.getUserId())
			.orElseThrow(() -> new IllegalStateException("채팅방에 참여한 이력이 없습니다."));

		LocalDateTime joinedAt = chatUser.getJoinedAt();

		return messageRepository.findByChatId_IdAndCreatedAtAfter(chatId, joinedAt)
				   .stream().map(MessageResponseDTO::from).toList();

	}


}
