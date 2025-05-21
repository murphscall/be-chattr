package com.kimje.chat.auth.service;

import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.security.jwt.JwtTokenProvider;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.enums.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final RedisService redisService;
	private final JwtTokenProvider jwtTokenProvider;

	public TokenService(RedisService redisService, JwtTokenProvider jwtTokenProvider) {
		this.redisService = redisService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public void createAccessToken(Long userId, UserRole role, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.createToken(userId, role);
		setAccessTokenCookie(accessToken, response);
	}

	public void createAndSaveRefreshToken(Long userId, HttpServletResponse response) {
		String refreshToken = UUID.randomUUID().toString();

		redisService.set("refresh:" + refreshToken, userId.toString(), 14, TimeUnit.DAYS);

		setRefreshTokenCookie(refreshToken, response);
	}

	public boolean validateRefreshToken(Long userId, String refreshToken) {
		String storedToken = redisService.get("refresh:" + refreshToken);
		return storedToken.equals(userId.toString());
	}

	public void deleteRefreshToken(String refreshToken) {
		System.out.println("삭제 전 존재 여부: " + redisService.get("refresh:" + refreshToken));
		redisService.delete("refresh:" + refreshToken);
		System.out.println("삭제 후 존재 여부: " + redisService.get("refresh:" + refreshToken));
	}

	private void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(60 * 15)
			.sameSite("Lax")
			.build();
		response.addHeader("Set-Cookie", cookie.toString());
	}

	private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(Duration.ofDays(14))
			.sameSite("Lax")
			.build();
		response.addHeader("Set-Cookie", cookie.toString());
	}
}
