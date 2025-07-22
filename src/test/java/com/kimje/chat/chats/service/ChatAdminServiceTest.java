package com.kimje.chat.chats.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.kimje.chat.chats.dto.ChatRequestDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.event.UserKickChatEvent;
import com.kimje.chat.chats.exception.ChatBanAccessDeniedException;
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
	ApplicationEventPublisher eventPublisher;

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
	void setUp(){
		chatId = 1L;
		targetUserId = 2L;
		// given
		User user = User.builder()
			.id(1L)
			.email("wlsgnwkd22@gmail.com")
			.role(UserRole.ROLE_USER)
			.name("jinhoo")
			.password("a12345")
			.build();

		User user2 = User.builder()
			.id(targetUserId)
			.email("kimjeanmia22@gmail.com")
			.name("jaeuk")
			.role(UserRole.ROLE_USER)
			.password("a123456")
			.build();

		Chat chat = Chat.builder()
			.id(chatId)
			.build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		authUser = (AuthUser) customUserDetails;


		masterUser = ChatUser.builder()
			.id(authUser.getUserId())
			.chat(chat)
			.user(user)
			.role(ChatRole.MASTER)
			.build();
		targetUser = ChatUser.builder()
			.id(targetUserId)
			.chat(chat)
			.user(user2)
			.role(ChatRole.MEMBER)
			.build();
	}

	@Test
	@DisplayName("성공: 방장이 유저의 역할을 매니저로 승격시킨다.")
	void changeRole_Success_PromoteToAdmin() {
		ChatRole newRole = ChatRole.MANAGER;
		ChatRequestDTO.ChangeRole requestDto = new ChatRequestDTO.ChangeRole();
		requestDto.setRole(newRole);

		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		when(chatUserRepository.findByChatIdAndUserId(chatId, targetUserId)).thenReturn(Optional.of(targetUser));

		// When
		chatAdminService.changeRole(authUser.getUserId(), chatId, targetUserId, requestDto);

		// Then
		assertThat(targetUser.getRole()).isEqualTo(newRole);
		verify(chatUserRepository).save(targetUser);
	}

	@Test
	@DisplayName("성공: 방장이 매니저(MANAGER)를 일반 유저(USER)로 강등시킨다")
	void changeRole_Success_DemoteToUser() {
		// Given
		// 테스트를 위해 대상 유저의 역할을 MANAGER로 미리 변경
		targetUser.setRole(ChatRole.MANAGER);
		ChatRole newRole = ChatRole.MEMBER;
		ChatRequestDTO.ChangeRole requestDto = new ChatRequestDTO.ChangeRole();
		requestDto.setRole(newRole);

		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		when(chatUserRepository.findByChatIdAndUserId(chatId, targetUserId)).thenReturn(Optional.of(targetUser));

		// When
		chatAdminService.changeRole(authUser.getUserId(), chatId, targetUserId, requestDto);

		// Then
		assertThat(targetUser.getRole()).isEqualTo(newRole);
		verify(chatUserRepository).save(targetUser);
	}

	@Test
	@DisplayName("실패: 방장이 아닌 유저가 역할 변경을 시도하면 예외가 발생한다")
	void changeRole_Fail_NotMaster() {
		// Given
		// 요청자의 역할을 MANAGER로 설정하여 방장이 아닌 상황을 만듦
		masterUser.setRole(ChatRole.MANAGER);
		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		ChatRequestDTO.ChangeRole requestDto = new ChatRequestDTO.ChangeRole();
		requestDto.setRole(ChatRole.MASTER);

		// When & Then
		// '권한이 없습니다.' 라는 예외가 발생하는지 검증
		assertThatThrownBy(() -> chatAdminService.changeRole(authUser.getUserId(), chatId, targetUserId, requestDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("권한이 없습니다.");
	}

	@Test
	@DisplayName("실패: 방장(MASTER) 역할을 다른 유저에게 부여하려고 시도하면 예외가 발생한다")
	void changeRole_Fail_CannotAssignMaster() {
		// Given
		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		ChatRequestDTO.ChangeRole requestDto = new ChatRequestDTO.ChangeRole();
		requestDto.setRole(ChatRole.MASTER);

		// When & Then
		// '방장 권한은 부여할 수 없습니다.' 라는 예외가 발생하는지 검증
		assertThatThrownBy(() -> chatAdminService.changeRole(authUser.getUserId(), chatId, targetUserId, requestDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("방장 권한은 부여할 수 없습니다.");
	}

	@Test
	@DisplayName("실패: 자기 자신의 역할을 변경하려고 시도하면 예외가 발생한다")
	void changeRole_Fail_ChangeOwnRole() {
		// Given
		Long selfId = authUser.getUserId(); // 자기 자신의 ID
		ChatRequestDTO.ChangeRole requestDto = new ChatRequestDTO.ChangeRole();
		requestDto.setRole(ChatRole.MANAGER);

		// When & Then
		// '자신의 권한은 변경할 수 없습니다.' 라는 예외가 발생하는지 검증
		assertThatThrownBy(() -> chatAdminService.changeRole(selfId, chatId, selfId, requestDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("자신의 권한은 변경할 수 없습니다.");
	}


	@Test
	@DisplayName("실패: 자기 자신을 추방하려고 시도하면 예외가 발생한다")
	void kickUser_Fail_KickSelf() {
		// Given
		Long selfId = authUser.getUserId();
		when(chatUserRepository.findByChatIdAndUserId(chatId, selfId)).thenReturn(Optional.of(masterUser));

		// When & Then
		assertThatThrownBy(() -> chatAdminService.kickUser(chatId, selfId, authUser))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("자신은 추방할 수 없습니다.");
	}

	@Test
	@DisplayName("실패: 방장이 아닌 유저가 추방을 시도하면 예외가 발생한다")
	void kickUser_Fail_NotMaster() {
		// Given
		// 요청자를 일반 MANAGER로 변경
		masterUser.setRole(ChatRole.MANAGER);
		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		when(chatUserRepository.findByChatIdAndUserId(chatId, targetUserId)).thenReturn(Optional.of(targetUser));

		// When & Then
		assertThatThrownBy(() -> chatAdminService.kickUser(chatId, targetUserId, authUser))
			.isInstanceOf(ChatBanAccessDeniedException.class)
			.hasMessage("추방 권한이 없습니다.");
	}

	@Test
	@DisplayName("실패: 추방 대상 유저가 채팅방에 없으면 예외가 발생한다")
	void kickUser_Fail_TargetNotFound() {
		// Given
		when(chatUserRepository.findByChatIdAndUserId(chatId, authUser.getUserId())).thenReturn(Optional.of(masterUser));
		// DB에서 targetUser를 찾지 못하는 상황을 Mocking
		when(chatUserRepository.findByChatIdAndUserId(chatId, targetUserId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> chatAdminService.kickUser(chatId, targetUserId, authUser))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("목록에 없는 참여자 입니다.");
	}


	@Test
	@DisplayName("유저 추방 성공 시 삭제 쿼리 호출 및 이벤트 호출")
	void kickUser_Success(){

		when(chatUserRepository.findByChatIdAndUserId(chatId,authUser.getUserId())).thenReturn(Optional.of(masterUser));
		when(chatUserRepository.findByChatIdAndUserId(chatId,targetUserId)).thenReturn(Optional.of(targetUser));

		chatAdminService.kickUser(chatId,targetUserId,authUser);

		verify(chatUserRepository).deleteByUserIdAndChatId(targetUserId,chatId);
		verify(eventPublisher).publishEvent(any(UserKickChatEvent.class));

	}

	@Test
	@DisplayName("실패: 유저 추방 요청 시 요청자가 채팅방에 없으면 예외 발생")
	void kickUser_Fail_RequesterNotInChat() {
		// Given
		when(chatUserRepository.
			findByChatIdAndUserId(chatId, authUser.getUserId()))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> chatAdminService.kickUser(chatId, targetUserId, authUser)).isInstanceOf(IllegalStateException.class)
			.hasMessage("채팅방 참여자가 아닙니다.");
	}
}