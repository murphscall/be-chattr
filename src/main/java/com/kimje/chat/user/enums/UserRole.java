package com.kimje.chat.user.enums;

import lombok.Getter;

@Getter
public enum UserRole {
	ROLE_ADMIN("어드민"),
	ROLE_USER("유저");

	private final String desc;

	UserRole(String desc) {
		this.desc = desc;
	}
}
