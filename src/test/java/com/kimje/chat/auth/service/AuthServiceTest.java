package com.kimje.chat.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	AuthService authService;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	TokenService tokenService;

	@Mock
	CookieUtil cookieUtil;

	@Mock
	UserService userService;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HttpServletRequest request;

	@Mock
	private Authentication authentication;

	@Mock
	private CustomUserDetails userDetails;

	private LoginDTO.Request loginRequest;
	private UserResponseDTO.Info userInfo;

	@BeforeEach
		// 🎯 추가: 초기화 메서드
	void setUp() {
		// 🎯 1. loginRequest 초기화
		loginRequest = new LoginDTO.Request();
		loginRequest.setEmail("test123@gmail.com");
		loginRequest.setPassword("test123");

		// 🎯 2. userInfo 초기화
		userInfo = UserResponseDTO.Info.builder()
			.userId(1L)
			.email("test123@gmail.com")
			.name("테스트유저")
			.build();

		Mockito.reset(authenticationManager, tokenService, userService);
	}

	@AfterEach
	void tearDown() {
		// 🎯 각 테스트 후에 정리
		Mockito.reset(authenticationManager, tokenService, userService);
	}

	@Test
	@DisplayName("로그인 성공")
	void loginUser() {
		Long userId = 1L;
		UserRole role = UserRole.ROLE_USER;

		when(authenticationManager.authenticate(
			any(UsernamePasswordAuthenticationToken.class)
		)).thenReturn(authentication);

		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(userDetails.getUserId()).thenReturn(userId);
		when(userDetails.getRole()).thenReturn(role);

		when(userService.getUserInfo(userId)).thenReturn(userInfo);


		UserResponseDTO.Info result = authService.loginUser(loginRequest, response);

		verify(tokenService).createAccessToken(userId , role , response);
		verify(tokenService).createAndSaveRefreshToken(userId , response);

		verify(userService).getUserInfo(userId);

		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(userInfo.getUserId());
		assertThat(result.getEmail()).isEqualTo(userInfo.getEmail());

	}

	@Test
	@DisplayName("로그아웃 성공")
	void logoutUser() {
		//given
		String refreshToken = String.valueOf(UUID.randomUUID());
		Cookie refreshCookie = new Cookie("refreshToken",refreshToken);
		Cookie[] cookies = {refreshCookie};

		when(request.getCookies()).thenReturn(cookies);
		when(tokenService.validateRefreshToken(refreshToken)).thenReturn(true);

		authService.logoutUser(request , response);
		verify(tokenService).validateRefreshToken(refreshToken);
		verify(tokenService).deleteRefreshToken(refreshToken);
	}
	@Test
	@DisplayName("로그아웃 성공 - 쿠키가 없는 경우에도 로그아웃됨")
	void logout_Success_NoCookie() {
		// given
		when(request.getCookies()).thenReturn(null); // 쿠키 없음

		// when
		authService.logoutUser(request, response);

		// then
		verify(tokenService, never()).validateRefreshToken(any());
		verify(tokenService, never()).deleteRefreshToken(any());


		verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
	}
}