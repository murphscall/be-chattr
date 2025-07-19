package com.kimje.chat.chats.service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.event.UserJoinedChatEvent;
import com.kimje.chat.chats.event.UserLeftChatEvent;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatCommandService {

	private static final int MAX_OPEN_CHATS_PER_USER = 5;
	private static final int MAX_PARTICIPANTS = 100;
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRepository;
	private final ChatUserRepository chatUserRepository;
	private final EntityManager em;
	private final ApplicationEventPublisher eventPublisher;

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


	@Transactional
	@CacheEvict(value = "chatMembers", key = "#chatId")
	public boolean joinUser(Long chatId, Long userId) {

		Chat chat = chatRepository.findById(chatId).
			orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅방입니다."));
		User user = em.getReference(User.class, userId);

		boolean existing = chatUserRepository.existsByChatIdAndUserId(chatId,userId );
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
		eventPublisher.publishEvent(new UserJoinedChatEvent(this , chatId , user.getName()));
		return true;
	}

	@Transactional
	@CacheEvict(value = "chatMembers", key = "#chatId")
	public void exitUser(Long chatId, Long userId) {
		ChatUser chatUser = chatUserRepository.findByChatIdAndUserId(chatId, userId)
			.orElseThrow(() -> new IllegalStateException("채팅방에 참여하고 있지 않습니다."));

		String leaveUserName = chatUser.getUser().getName();

		boolean isMaster = chatUser.getRole() == ChatRole.MASTER;

		// 일반 유저 퇴장
		if (!isMaster) {
			chatUserRepository.delete(chatUser);
			eventPublisher.publishEvent(new UserLeftChatEvent(this , chatId , leaveUserName));
			return;
		}
		// 방장일 때
		// 채팅방에 참여중인 모든 유저
		int count = chatUserRepository.countByChatId(chatId);
		// 참여수가 1명이거나 작으면 채팅방 삭제 / 채팅 목록 테이블도 자동 삭제
		if (count <= 1) {
			chatUserRepository.delete(chatUser);
			chatRepository.deleteById(chatId);
			eventPublisher.publishEvent(new UserLeftChatEvent(this , chatId , leaveUserName));
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
		eventPublisher.publishEvent(new UserLeftChatEvent(this , chatId , leaveUserName));
	}


}
