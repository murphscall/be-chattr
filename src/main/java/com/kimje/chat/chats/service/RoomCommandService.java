package com.kimje.chat.chats.service;
import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomCommandService {

	private static final int MAX_OPEN_CHATS_PER_USER = 5;
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRepository;
	private final ChatUserRepository chatUserRepository;

	@Transactional
	public ChatResponseDTO.ChatInfo createChatRoom(Long userId, ChatRequestDTO.Create dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		validateOpenChatLimit(userId);

		// 3. Chat 엔티티 생성
		Chat chat = dto.toEntity();
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


	public void validateOpenChatLimit(Long userId) {
		int count = chatUserRepository.countByUserIdAndRole(userId, ChatRole.MASTER);

		if (count >= MAX_OPEN_CHATS_PER_USER) {
			throw new IllegalStateException("오픈 채팅방은 최대 5개까지만 생성할 수 있습니다. 추가로 생성을 원할 시에 기존의 방을 삭제해주세요");
		}
	}

}
