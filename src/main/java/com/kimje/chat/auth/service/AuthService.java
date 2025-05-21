package com.kimje.chat.auth.service;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	private final UserService userService;

	public UserResponseDTO.Info loginUser(LoginDTO.Request request, HttpServletResponse response) {
		log.info("[LOGIN] 인증 시작 : {}", request.getEmail());

		Authentication	authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					request.getEmail(), request.getPassword()
				)
			);
		log.info("[LOGIN] 인증 완료 : {}", request.getEmail());

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UserRole role = userDetails.getRole();
		// 여기 까지 user 로그인 정보 확인

		// user 정보로 accessToken 생성
		tokenService.createAccessToken(userDetails.getUserId(), role, response);
		// refresh 토큰 생성 후 쿠키에 설정
		tokenService.createAndSaveRefreshToken(userDetails.getUserId(), response);
		// user 정보 가져오기
		log.info("[LOGIN] 토큰 발급 완료 : {}", request.getEmail());
		// 유저 정보 반환
		return userService.getUserInfo(userDetails.getUserId());
	}

	public void logoutUser(AuthUser authUser, HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = CookieUtil.getCookie(request, "refreshToken");

		if (refreshToken != null && tokenService.validateRefreshToken(authUser.getUserId(), refreshToken)) {
			tokenService.deleteRefreshToken(refreshToken);

			ResponseCookie expiredAccessCookie = CookieUtil.deleteCookie("accessToken");
			ResponseCookie expiredRefreshCookie = CookieUtil.deleteCookie("refreshToken");

			response.addHeader("Set-Cookie", expiredAccessCookie.toString());
			response.addHeader("Set-Cookie", expiredRefreshCookie.toString());
		}
	}

}
