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
import org.springframework.context.ApplicationEventPublisher; // 추가

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ChatUserServiceTest {

	@InjectMocks
	ChatCommandService chatCommandService;

	// @InjectMocks // 이 테스트에서는 ChatQueryService를 테스트하지 않으므로 제거합니다.
	// ChatQueryService chatQueryService;

	@Mock
	ChatUserRepository chatUserRepository;

	@Mock
	ChatRoomRepository chatRoomRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	EntityManager em;

	// ⬇️ 1. ApplicationEventPublisher Mock 객체 추가
	@Mock
	ApplicationEventPublisher eventPublisher;


	private static final Long chatId = 1L;
	private static final Long userId = 1L;

	@Test
	@DisplayName("채팅방 신규 입장 시 성공")
	void joinUser() {
		// given
		int memberCount = 97;
		User mockUser = new User(); // 가짜 User 객체 생성
		mockUser.setId(userId);
		mockUser.setName("테스트유저");

		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId, userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);
		// ⬇️ 2. em.getReference가 호출될 때, 우리가 만든 가짜 User 객체를 반환하도록 정의
		when(em.getReference(User.class, userId)).thenReturn(mockUser);

		// when
		chatCommandService.joinUser(chatId, userId);

		// then
		verify(chatUserRepository).save(any(ChatUser.class));
		// ⬇️ 3. eventPublisher의 publishEvent 메서드가 호출되었는지 검증
		verify(eventPublisher).publishEvent(any());
	}

	@Test
	@DisplayName("기존 참여자 채팅방 입장 시")
	void againJoinUser() {
		// given
		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(true);
		// ⬇️ joinUser 메서드에서 user.getName()을 호출하므로, 이 테스트에서도 user 객체를 준비해줘야 합니다.
		when(em.getReference(User.class, userId)).thenReturn(new User());


		// when & then
		assertThat(chatCommandService.joinUser(chatId,userId)).isFalse();
	}

	@Test
	@DisplayName("채팅방 입장 요청 시 인원 초과 예외")
	public void max_participants(){
		// given
		int memberCount = 100;

		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);
		// ⬇️ 예외가 발생하기 전에 user 객체가 필요하므로 준비합니다.
		when(em.getReference(User.class, userId)).thenReturn(new User());


		// when & then
		assertThrows(IllegalStateException.class, () -> chatCommandService.joinUser(chatId,userId));
	}



	@Test
	@DisplayName("채팅방에 참여 하지 않은 유저 퇴장 시도 시 예외 발생")
	void exitUser_UserNotInChat_ThrowsException() {
		// given
		when(chatUserRepository.findByChatIdAndUserId(chatId, userId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> chatCommandService.exitUser(chatId, userId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("채팅방에 참여하고 있지 않습니다.");
	}

	@Test
	@DisplayName("일반 유저 퇴장 - 정상 삭제")
	public void exitUser_RegularMember_DeletesSuccessfully(){
		// given
		ChatUser chatUser = createChatUser(userId,ChatRole.MEMBER);
		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(chatUser));

		// when
		chatCommandService.exitUser(chatId,userId);

		// then
		verify(chatUserRepository).delete(chatUser);
		verify(chatUserRepository, never()).countByChatId(anyLong());
		verify(chatRoomRepository, never()).deleteById(anyLong());
		verify(eventPublisher).publishEvent(any()); // 이벤트 발행 검증 추가
	}

	@Test
	@DisplayName("방장 유저 퇴장 - 혼자 남은 경우")
	public void exitUser_MasterMember_DeleteSuccessfully(){
		// given
		ChatUser chatUser = createChatUser(userId,ChatRole.MASTER);
		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(chatUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(1);

		// when
		chatCommandService.exitUser(chatId,userId);

		// then
		verify(chatUserRepository).delete(chatUser);
		verify(chatRoomRepository).deleteById(chatId);
		verify(eventPublisher).publishEvent(any()); // 이벤트 발행 검증 추가
	}

	@Test
	@DisplayName("방장 유저 퇴장(매니저 있는 경우) - 매니저에게 권한 양도")
	public void exitUser_MasterMember_TransferToManager(){
		// given
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);
		ChatUser managerUser = createChatUser(2L, ChatRole.MANAGER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.of(managerUser));

		// when
		chatCommandService.exitUser(chatId,userId);

		// then
		assertThat(managerUser.getRole()).isEqualTo(ChatRole.MASTER);
		verify(chatUserRepository).save(managerUser);
		verify(chatUserRepository).delete(masterUser);
		verify(eventPublisher).publishEvent(any()); // 이벤트 발행 검증 추가
	}

	@Test
	@DisplayName("방장 유저 퇴장(매니저 없는 경우) - 유저에게 권한 양도")
	public void exitUser_MasterMember_TransferToMember(){
		// given
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);
		ChatUser memberUser = createChatUser(3L, ChatRole.MEMBER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.empty());
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MEMBER)).thenReturn(Optional.of(memberUser));

		// when
		chatCommandService.exitUser(chatId,userId);

		// then
		assertThat(memberUser.getRole()).isEqualTo(ChatRole.MASTER);
		verify(chatUserRepository).save(memberUser);
		verify(chatUserRepository).delete(masterUser);
		verify(eventPublisher).publishEvent(any()); // 이벤트 발행 검증 추가
	}

	@Test
	@DisplayName("권한 양도할 매니저도 멤버도 없다면 예외 발생")
	public void failed_transfer_to_member(){
		// given
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.empty());
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MEMBER)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			chatCommandService.exitUser(chatId,userId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("위임할 대상이 없습니다.");
	}

	// 테스트 헬퍼 메서드
	private ChatUser createChatUser(Long userId,ChatRole role) {
		Chat chat = new Chat();
		chat.setId(chatId);
		User user = new User();
		user.setId(userId);
		user.setName("테스트유저"); // ⬇️ leaveUserName을 위해 이름 설정

		ChatUser chatUser = new ChatUser();
		chatUser.setChat(chat);
		chatUser.setUser(user);
		chatUser.setRole(role);
		return chatUser;
	}
}