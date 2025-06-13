package com.kimje.chat.global.security.jwt;

import com.kimje.chat.global.exception.JwtInvalidTokenException;
import com.kimje.chat.global.exception.JwtTokenExpiredException;
import com.kimje.chat.global.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	// 모든 요청에서 jwt 토큰을 꺼내서 검증 해야함.
	// 인증 정보를 securityContext에 넣어야함.

	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (!uri.equals("/ws/info")) {
			log.info("🟢[JWT AUTH FILTER] 요청 | {} {} from {}", request.getMethod(), uri, request.getRemoteAddr());
		}
		//    String token = jwtTokenProvider.resolveToken(request);
		String token = CookieUtil.getCookie(request, "accessToken");

		try {
			if (token != null) {
				log.debug("🔵[AccessToken] 발견: 토큰 길이 = {}", token.length());
				jwtTokenProvider.validateTokenOrThrow(token);  // 예외 던짐
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.debug("🔵[AccessToken] 검증 완료");
			}else{
				log.debug("🟡[AccessToken] 없음");
			}
		} catch (JwtTokenExpiredException e) {
			log.warn("🟡[ACCESS TOKEN] 만료 | uri={} | message={}", request.getRequestURI(), e.getMessage());
			sendErrorResponse(response, "ACCESS_TOKEN_EXPIRED");
			sendErrorResponse(response, "ACCESS_TOKEN_EXPIRED");
			return;
		} catch (JwtInvalidTokenException e) {
			log.warn("🟡[ACCESS TOKEN] 무효 | uri={} | message={}", request.getRequestURI(), e.getMessage());
			sendErrorResponse(response, "INVALID_ACCESS_TOKEN");
			return;
		}
		filterChain.doFilter(request, response);
	}

	private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}
}
