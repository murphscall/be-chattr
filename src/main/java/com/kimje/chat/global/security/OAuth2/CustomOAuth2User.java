package com.kimje.chat.global.security.OAuth2;

import com.kimje.chat.user.enums.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User, AuthUser {

	private final long userId;
	private final String email;
	private final UserRole role;
	private final String name;
	private final String provider;
	private final String providerId;
	private final Map<String, Object> attributes;

	public CustomOAuth2User(long userId, String email, UserRole role,String name, String provider, String providerId,
		Map<String, Object> attributes) {
		this.userId = userId;
		this.email = email;
		this.role = role;
		this.name = name;
		this.provider = provider;
		this.providerId = providerId;
		this.attributes = attributes;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getNickname() {
		return name;
	}

	@Override
	public String getLoginType() {
		return provider;
	}

	@Override
	public UserRole getRole() {
		return role;
	}

	// 시큐리티 형식적 구현
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getName() {
		return email;
	}
}
