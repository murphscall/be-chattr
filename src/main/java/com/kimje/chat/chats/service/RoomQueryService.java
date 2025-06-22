package com.kimje.chat.chats.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.global.response.PageResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomQueryService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatUserRepository chatUserRepository;

	@Transactional(readOnly = true)
	public PageResponse<ChatResponseDTO.ChatInfo> getChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> chats = chatRoomRepository.getChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(chats);
		return pageResponse;
	}
	@Transactional(readOnly = true)
	public PageResponse<ChatResponseDTO.ChatInfo> getHotChats(Pageable pageable) {
		Page<ChatResponseDTO.ChatInfo> hotChats = chatRoomRepository.findHotChats(pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(hotChats);
		return pageResponse;
	}
	@Transactional(readOnly = true)
	public PageResponse<ChatResponseDTO.ChatInfo> getMyChats(AuthUser authUser ,Pageable pageable) {

		Page<ChatResponseDTO.ChatInfo> myChats = chatUserRepository.findMyChatsWithCount(authUser.getUserId(), pageable);
		PageResponse<ChatResponseDTO.ChatInfo> pageResponse = PageResponse.from(myChats);
		return pageResponse;
	}
}
