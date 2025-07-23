package com.kimje.chat.chats.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.global.response.PageResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;


	public PageResponse<ChatResponseDTO.ChatInfo> getChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> chats = chatRoomRepository.getChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(chats);
		return pageResponse;
	}

	public PageResponse<ChatResponseDTO.ChatInfo> getHotChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> hotChats = chatRoomRepository.findHotChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(hotChats);
		return pageResponse;
	}

	public PageResponse<ChatResponseDTO.ChatInfo> getMyChats(AuthUser authUser ,Pageable pageable) {

		Page<ChatResponseDTO.ChatInfo> myChats = chatUserRepository.findMyChatsWithCount(authUser.getUserId(), pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(myChats);
		return pageResponse;
	}

	@Cacheable(value = "chatMembers", key = "#chatId")
	public List<ChatResponseDTO.ChatUserInfo> getMembers(Long chatId) {
		List<ChatUser> members = chatUserRepository.findAllWithUserByChatId(chatId);

		return members.stream()
			.map(cu -> new ChatResponseDTO.ChatUserInfo(
				cu.getUser().getId(),
				cu.getUser().getName(),
				cu.getRole()
			))
			.collect(Collectors.toList());
	}

	@Cacheable(value = "createdChats" , key = "#userId")
	public List<ChatResponseDTO.ChatInfo> getCreateByMeChats(Long userId) {
		return chatUserRepository.findChatsByUserIdAndRole(userId, ChatRole.MASTER);
	}
}
