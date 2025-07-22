package com.kimje.chat.auth.controller;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.auth.service.AuthService;
import com.kimje.chat.auth.service.TokenService;
import com.kimje.chat.global.exception.RefreshTokenNotFoundException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.security.jwt.JwtTokenProvider;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;
import com.kimje.chat.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="사용자 및 인증 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;
	private final RedisService redisService;
	private final UserService userService;

	public AuthController(AuthService authService, TokenService tokenService,
		RedisService redisService,  UserService userService) {
		this.authService = authService;
		this.tokenService = tokenService;
		this.redisService = redisService;
		this.userService = userService;
	}

	@GetMapping("/authentication")
	public ResponseEntity<ApiResponse<?>> authentication(@AuthenticationPrincipal AuthUser authUser) {
		if (authUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증되지 않은 사용자"));
		}

		UserResponseDTO.Info info = userService.getUserInfo(authUser.getUserId());

		return ResponseEntity.ok().body(ApiResponse.success(info));

	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO.Request dto, HttpServletResponse response) {
		UserResponseDTO.Info loginResponse = authService.loginUser(dto, response);

		// 유저정보와 액세스 토큰은 반환하고 refresh 토큰은 쿠키로 전송
		return ResponseEntity.ok().body(ApiResponse.success(loginResponse));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request,
		HttpServletResponse response) {

		authService.logoutUser(request, response);

		return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(
		HttpServletRequest request,
		HttpServletResponse response) {
		log.info("🟢[REFRESH] 요청 도착 | IP: {} | URI: {}", request.getRemoteAddr(), request.getRequestURI());


		// 1. 쿠키에서 refreshToken(UUID) 추출
		String refreshToken = CookieUtil.getCookie(request, "refreshToken");

		if (refreshToken == null) {
			throw new RefreshTokenNotFoundException("리프레쉬 토큰이 존재하지 않음");
		}


		// 2. Redis에서 userId 조회
		// 키 refresh:UUID , 값 : userId , 정보 등
		log.debug("🔵[REFRESH] Redis 조회 시도 | key = {}", "refresh:" + refreshToken);
		String userIdStr = redisService.get("refresh:" + refreshToken);
		if (userIdStr == null) {
			log.warn("🟡[REFRESH] 유효하지 않은 리프레시 토큰 | key = {}", "refresh:" + refreshToken);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Invalid refresh token"));
		}
		Long userId = Long.parseLong(userIdStr);

		UserResponseDTO.Info userInfo = userService.getUserInfo(userId);
		log.info("🟢[REFRESH] 리프레쉬 토큰 확인 완료 | userId={}", userId);
		// 3. 새 accessToken 발급 후 쿠키로 응답
		// 기존 리프레쉬 토큰 삭제 후 새로운 리프레쉬 토큰 발급
		tokenService.createAccessToken(userInfo.getUserId(), UserRole.ROLE_USER, response);
		tokenService.deleteRefreshToken(refreshToken);
		tokenService.createAndSaveRefreshToken(userInfo.getUserId(), response);

		log.info("🟢[REFRESH] 액세스/리프레시 토큰 재발급 완료 | userId={}", userId);
		return ResponseEntity.ok().body(ApiResponse.success("리프레쉬 토큰 발급"));
	}
}
