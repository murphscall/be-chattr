package com.kimje.chat.chats.service;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.service.message.SystemMessageService;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatAdminService {

	private final ChatUserRepository chatUserRepository;
	private final ChatRoomRepository chatRepository;
	private final SystemMessageService systemMessageService;

	public ChatAdminService(ChatUserRepository chatUserRepository, ChatRoomRepository chatRepository , SystemMessageService systemMessageService) {
		this.chatUserRepository = chatUserRepository;
		this.chatRepository = chatRepository;
		this.systemMessageService = systemMessageService;
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

	@Transactional
	public void kickUser(Long chatId, Long userId , AuthUser authUser) {
		// 1. 현재 요청한  유저가 master의 권한을 갖고 있는지 ?
		ChatUser masterUser = chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())
			.orElseThrow(() -> new IllegalStateException("채팅방 참여자가 아닙니다."));

		if(userId.equals(authUser.getUserId())){
			throw new IllegalStateException("자신은 추방할 수 없습니다.");
		}

		if(masterUser.getRole() != ChatRole.MASTER){
			throw new IllegalStateException("추방 권한이 없습니다.");
		}

		chatUserRepository.deleteByUserIdAndChatId(userId, chatId);
		systemMessageService.sendKickNotice(chatId, userId);

	}
}
