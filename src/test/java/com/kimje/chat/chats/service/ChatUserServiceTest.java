package com.kimje.chat.chats.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ChatUserServiceTest {

	@InjectMocks
	ChatUserService chatUserService;

	@Mock
	ChatUserRepository chatUserRepository;

	@Mock
	ChatRoomRepository chatRoomRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	EntityManager em;

	@Test
	@DisplayName("채팅방 신규 입장 시 성공")
	void joinUser() {
		Long chatId = 1L;
		Long userId = 1L;
		int memberCount = 97;

		when(chatRoomRepository.findById(userId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);

		chatUserService.joinUser(chatId,userId);

		verify(chatUserRepository).save(any(ChatUser.class));
	}

	@Test
	@DisplayName("기존 참여자 채팅방 입장 시")
	void againJoinUser() {
		Long chatId = 1L;
		Long userId = 1L;

		when(chatRoomRepository.findById(userId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(true);


		assertThat(chatUserService.joinUser(chatId,userId)).isFalse();
	}

	@Test
	@DisplayName("채팅방 입장 요청 시 인원 초과 예외")
	public void max_participants(){
		Long chatId = 1L;
		Long userId = 1L;
		int memberCount = 100;

		when(chatRoomRepository.findById(userId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);


		assertThrows(IllegalStateException.class, () -> chatUserService.joinUser(chatId,userId));
	}



	@Test
	void exitUser() {
	}

	@Test
	void getMembers() {
	}

	@Test
	void getCreateByMeChats() {
	}
}