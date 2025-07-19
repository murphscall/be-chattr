package com.kimje.chat.chats.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class UserJoinedChatEvent extends ApplicationEvent {
	private final Long chatId;
	private final String userName;

	/**
	 * UserJoinedChatEvent 생성자
	 *
	 * @param source     이벤트를 발생시킨 객체 (보통 this를 전달)
	 * @param chatId     사용자가 참여한 채팅방 ID
	 * @param userName   참여한 사용자의 이름 (알림 메시지에 사용)
	 */
	public UserJoinedChatEvent(Object source, Long chatId, String userName) {
		super(source); // 2. 부모 클래스인 ApplicationEvent의 생성자를 호출해야 합니다.
		this.chatId = chatId;
		this.userName = userName;
	}
}
