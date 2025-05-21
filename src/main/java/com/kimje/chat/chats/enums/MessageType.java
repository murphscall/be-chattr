package com.kimje.chat.chats.enums;

import lombok.Getter;

@Getter
public enum MessageType {
	TEXT,
	IMAGE,
	SYSTEM,
	FILE,
	NOTICE_JOIN,
	NOTICE_LEAVE,
	MENTION,
}
