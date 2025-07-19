package com.kimje.chat.chats.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserKickChatEvent extends ApplicationEvent {

	private final Long chatId;
	private final String kickedUserName; // 강퇴당한 유저의 이름
	private final Long kickedUserId;     // 강퇴당한 유저의 ID

	public UserKickChatEvent(Object source, Long chatId, String kickedUserName, Long kickedUserId) {
		super(source);
		this.chatId = chatId;
		this.kickedUserName = kickedUserName;
		this.kickedUserId = kickedUserId;
	}
}
