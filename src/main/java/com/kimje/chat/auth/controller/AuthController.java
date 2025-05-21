package com.kimje.chat.auth.controller;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.auth.service.AuthService;
import com.kimje.chat.auth.service.TokenService;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.security.jwt.JwtTokenProvider;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final UserRepository userRepository;

	public AuthController(AuthService authService, TokenService tokenService, JwtTokenProvider jwtTokenProvider,
		RedisService redisService, UserRepository userRepository) {
		this.authService = authService;
		this.tokenService = tokenService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.userRepository = userRepository;
	}

	@GetMapping("/authentication")
	public ResponseEntity<ApiResponse<?>> authentication(@AuthenticationPrincipal AuthUser authUser) {

		if (authUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증되지 않은 사용자"));
		}
		User user = userRepository.findById(authUser.getUserId())
			.orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

		UserResponseDTO.Info info = new UserResponseDTO.Info(user.getId(),user.getEmail(),user.getName(),user.getPhone(),user.getCreatedAt());

		return ResponseEntity.ok().body(ApiResponse.success(info));

	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO.Request dto, HttpServletResponse response) {
		log.info("[LOGIN] 로그인 요청 : {}", dto.getEmail());
		UserResponseDTO.Info loginResponse = authService.loginUser(dto, response);

		// 유저정보와 액세스 토큰은 반환하고 refresh 토큰은 쿠키로 전송
		return ResponseEntity.ok().body(ApiResponse.success(loginResponse));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal AuthUser authUser, HttpServletRequest request,
		HttpServletResponse response) {

		authService.logoutUser(authUser, request, response);

		return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(
		HttpServletRequest request,
		HttpServletResponse response) {

		System.out.println("zzzz");

		// 1. 쿠키에서 refreshToken(UUID) 추출
		String refreshToken = CookieUtil.getCookie(request, "refreshToken");
		System.out.println(refreshToken + " refreshToken");
		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Refresh token not found"));
		}


		// 2. Redis에서 userId 조회
		// 키 refresh:UUID , 값 : userId , 정보 등
		String userIdStr = redisService.get("refresh:" + refreshToken);
		if (userIdStr == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Invalid refresh token"));
		}
		Long userId = Long.parseLong(userIdStr);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalStateException("유저가 존재하지 않습니다."));
		// 3. 새 accessToken 발급 후 쿠키로 응답
		// 기존 리프레쉬 토큰 삭제 후 새로운 리프레쉬 토큰 발급
		tokenService.createAccessToken(user.getId(), UserRole.ROLE_USER, response);
		System.out.println("refresh token : " + refreshToken);
		tokenService.deleteRefreshToken(refreshToken);
		tokenService.createAndSaveRefreshToken(user.getId(), response);

		return ResponseEntity.ok().body(ApiResponse.success("리프레쉬 토큰 발급"));
	}
}
