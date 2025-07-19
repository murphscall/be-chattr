package com.kimje.chat.chats.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class UserLeftChatEvent extends ApplicationEvent {
	private final Long chatId;
	private final String leaverUserName; // 나간 유저의 이름

	public UserLeftChatEvent(Object source, Long chatId, String leaverUserName) {
		super(source);
		this.chatId = chatId;
		this.leaverUserName = leaverUserName;
	}
}
