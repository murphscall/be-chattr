package com.kimje.chat.chats.enums;

import lombok.Getter;

@Getter
public enum ChatRole {
	MASTER("방장"),
	MANAGER("매니저"),
	MEMBER("멤버");

	private final String desc;

	ChatRole(String desc) {
		this.desc = desc;
	}
}
