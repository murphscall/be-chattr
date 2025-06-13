package com.kimje.chat.chats.service;

import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO.ChatUserInfo;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserService {

	private static final int MAX_PARTICIPANTS = 100;
	private final UserRepository userRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatRoomRepository chatRepository;
	private final EntityManager em;

	public boolean joinUser(Long chatId, Long userId) {

		Chat chat = chatRepository.findById(chatId).
				orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅방입니다."));
		User user = em.getReference(User.class, userId);

		boolean existing = chatUserRepository.existsByChatIdAndUserId(chatId, user.getId());
		if (existing) {
			return false;
		}
		int count = chatUserRepository.countByChatId(chatId);
		if (count >= MAX_PARTICIPANTS) {
			throw new IllegalStateException("채팅방 인원이 가득 찼습니다.");
		}


		ChatUser newEntry = ChatUser.builder()
			.chat(chat)
			.user(user)
			.role(ChatRole.MEMBER)
			.build();

		chatUserRepository.save(newEntry);
		return true;
	}

	public void exitUser(Long chatId, Long userId) {
		ChatUser chatUser = chatUserRepository.findByChatIdAndUserId(chatId, userId)
			.orElseThrow(() -> new IllegalStateException("채팅방에 참여하고 있지 않습니다."));

		boolean isMaster = chatUser.getRole() == ChatRole.MASTER;

		// 일반 유저 퇴장
		if (!isMaster) {
			chatUserRepository.delete(chatUser);
			return;
		}
		// 방장일 때
		// 채팅방에 참여중인 모든 유저
		int count = chatUserRepository.countByChatId(chatId);
		// 참여수가 1명이거나 작으면 채팅방 삭제 / 채팅 목록 테이블도 자동 삭제
		if (count <= 1) {
			chatUserRepository.delete(chatUser);
			chatRepository.deleteById(chatId);
			return;
		}

		// 참여자 수가 2명 이상이면
		// 참여자들 중 첫번째 매니저에게 권한 양도 그 후 방장 퇴장
		Optional<ChatUser> managerOpt = chatUserRepository.findFirstByChatIdAndRole(chatId, ChatRole.MANAGER);

		if (managerOpt.isPresent()) {
			ChatUser manager = managerOpt.get();
			manager.setRole(ChatRole.MASTER);
			chatUserRepository.save(manager);
		} else {
			// 매니저 없으면 멤버 중 아무나
			ChatUser fallback = chatUserRepository.findFirstByChatIdAndRole(chatId, ChatRole.MEMBER)
				.orElseThrow(() -> new IllegalStateException("위임할 대상이 없습니다."));
			fallback.setRole(ChatRole.MASTER);
			chatUserRepository.save(fallback);
		}

		chatUserRepository.delete(chatUser);
	}

	public List<ChatUserInfo> getMembers(Long chatId) {
		List<ChatUser> members = chatUserRepository.findAllByChatId(chatId);

		return members.stream()
			.map(cu -> new ChatUserInfo(
				cu.getUser().getId(),
				cu.getUser().getName(),
				cu.getRole()
			))
			.collect(Collectors.toList());
	}

	public List<ChatResponseDTO.ChatInfo> getCreateByMeChats(Long userId) {
		return chatUserRepository.findChatsByUserIdAndRole(userId,ChatRole.MASTER);
	}
}
