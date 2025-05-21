package com.kimje.chat.chats.service;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;

import org.springframework.stereotype.Service;

@Service
public class ChatAdminService {

	private final ChatUserRepository chatUserRepository;
	private final ChatRepository chatRepository;

	public ChatAdminService(ChatUserRepository chatUserRepository, ChatRepository chatRepository) {
		this.chatUserRepository = chatUserRepository;
		this.chatRepository = chatRepository;
	}

	public void changeRole(Long userId, Long chatId, Long targetId, ChatRequestDTO.ChangeRole dto) {
		// 자신이 자신을 승격시키고 내릴 수 없음 -
		// 타인을 방장 권한으로 올릴수 없음. (방장 권한은 방장이 나갈 때 자동으로 다음 매니저 혹은 유저들 중 하나로 방장이 결정됨.)

		if (userId.equals(targetId)) {
			throw new IllegalStateException("자신의 권한은 변경할 수 없습니다.");
		}

		ChatUser chatUser = chatUserRepository.findByChatIdAndUserId(chatId, userId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅 회원입니다."));

		ChatRole chatRole = chatUser.getRole();

		if (chatRole != ChatRole.MASTER) {
			throw new IllegalStateException("권한이 없습니다.");
		}

		if (dto.getRole() == ChatRole.MASTER) {
			throw new IllegalArgumentException("방장 권한은 부여할 수 없습니다.");
		}

		ChatUser targetUser = chatUserRepository.findByChatIdAndUserId(chatId, targetId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅 회원입니다."));

		targetUser.setRole(dto.getRole());

		chatUserRepository.save(targetUser);
	}
}
