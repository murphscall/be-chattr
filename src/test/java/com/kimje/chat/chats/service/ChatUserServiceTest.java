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

	@InjectMocks
	ChatQueryService chatQueryService;

	@Mock
	ChatUserRepository chatUserRepository;

	@Mock
	ChatRoomRepository chatRoomRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	EntityManager em;

	private static final Long chatId = 1L;
	private static final Long userId = 1L;

	@Test
	@DisplayName("채팅방 신규 입장 시 성공")
	void joinUser() {
		int memberCount = 97;

		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);

		chatCommandService.joinUser(chatId,userId);

		verify(chatUserRepository).save(any(ChatUser.class));
	}

	@Test
	@DisplayName("기존 참여자 채팅방 입장 시")
	void againJoinUser() {

		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(true);


		assertThat(chatCommandService.joinUser(chatId,userId)).isFalse();
	}

	@Test
	@DisplayName("채팅방 입장 요청 시 인원 초과 예외")
	public void max_participants(){
		int memberCount = 100;

		when(chatRoomRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
		when(chatUserRepository.existsByChatIdAndUserId(chatId,userId)).thenReturn(false);
		when(chatUserRepository.countByChatId(chatId)).thenReturn(memberCount);


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
		ChatUser chatUser = createChatUser(userId,ChatRole.MEMBER);
		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(chatUser));

		chatCommandService.exitUser(chatId,userId);

		verify(chatUserRepository).delete(chatUser);
		verify(chatUserRepository, never()).countByChatId(anyLong());
		verify(chatRoomRepository, never()).deleteById(anyLong());
	}

	@Test
	@DisplayName("방장 유저 퇴장 - 혼자 남은 경우")
	public void exitUser_MasterMember_DeleteSuccessfully(){
		ChatUser chatUser = createChatUser(userId,ChatRole.MASTER);
		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(chatUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(1);

		chatCommandService.exitUser(chatId,userId);
		verify(chatUserRepository).delete(chatUser);
		verify(chatRoomRepository).deleteById(chatId);
	}

	@Test
	@DisplayName("방장 유저 퇴장(매니저 있는 경우) - 매니저에게 권한 양도")
	public void exitUser_MasterMember_TransferToManager(){
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);
		ChatUser managerUser = createChatUser(2L, ChatRole.MANAGER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.of(managerUser));

		chatCommandService.exitUser(chatId,userId);

		assertThat(managerUser.getRole()).isEqualTo(ChatRole.MASTER);
		verify(chatUserRepository).save(managerUser);
		verify(chatUserRepository).delete(masterUser);

	}

	@Test
	@DisplayName("방장 유저 퇴장(매니저 없는 경우) - 유저에게 권한 양도")
	public void exitUser_MasterMember_TransferToMember(){
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);
		ChatUser memberUser = createChatUser(3L, ChatRole.MEMBER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.empty());
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MEMBER)).thenReturn(Optional.of(memberUser));


		chatCommandService.exitUser(chatId,userId);


		assertThat(memberUser.getRole()).isEqualTo(ChatRole.MASTER);

		verify(chatUserRepository).save(memberUser);
		verify(chatUserRepository).delete(masterUser);
	}

	@Test
	@DisplayName("권한 양도할 매니저도 멤버도 없다면 예외 발생")
	public void failed_transfer_to_member(){
		ChatUser masterUser = createChatUser(userId,ChatRole.MASTER);

		when(chatUserRepository.findByChatIdAndUserId(chatId,userId))
			.thenReturn(Optional.of(masterUser));
		when(chatUserRepository.countByChatId(chatId)).thenReturn(3);
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MANAGER)).thenReturn(Optional.empty());
		when(chatUserRepository.findFirstByChatIdAndRole(chatId,ChatRole.MEMBER)).thenReturn(Optional.empty());


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

		ChatUser chatUser = new ChatUser();
		chatUser.setChat(chat);
		chatUser.setUser(user);
		chatUser.setRole(role);
		return chatUser;
	}
}