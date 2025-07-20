package com.kimje.chat.chats.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.enums.UserRole;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ChatAdminServiceTest {


	@InjectMocks
	ChatAdminService chatAdminService;

	@Mock
	ChatUserRepository chatUserRepository;

	@Mock
	ChatRoomRepository chatRoomRepository;


	private Long chatId;

	private Long targetUserId ;

	private AuthUser authUser;

	private ChatUser masterUser;

	private ChatUser targetUser;


	@BeforeEach
	public  void setUp(){
		// given
		User user = User.builder()
			.id(1L)
			.email("wlsgnwkd22@gmail.com")
			.role(UserRole.ROLE_USER)
			.password("sy8583lk^^")
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		authUser = (AuthUser) customUserDetails;
		chatId = 1L;
		targetUserId = 2L;

		masterUser = ChatUser.builder()
			.id(authUser.getUserId())
			.role(ChatRole.MASTER)
			.build();
		targetUser = ChatUser.builder()
			.id(2L)
			.role(ChatRole.MEMBER)
			.build();
	}

	@Test
	void changeRole() {
	}



	@Test
	@DisplayName("유저 추방 요청 시 채팅방에 속한 상태가 아닐 때 예외 발생")
	public void kickUser_IllegalException(){
		when(chatUserRepository.
			findByChatIdAndUserId(chatId,authUser.getUserId()))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> chatAdminService.kickUser(chatId,targetUserId,authUser)).isInstanceOf(IllegalStateException.class)
			.hasMessage("채팅방 참여자가 아닙니다.");
	}
}