package com.kimje.chat.global.security.OAuth2;

import com.kimje.chat.user.enums.UserRole;

public interface AuthUser {
	Long getUserId();

	String getNickname();

	String getEmail();

	UserRole getRole();

	String getLoginType();
}
