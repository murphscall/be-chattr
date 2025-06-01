package com.kimje.chat.chats.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
	MENTION,
	INVITE,
	KICK,
	WARNING,
	SYSTEM,
	CUSTOM
}