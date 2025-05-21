package com.kimje.chat.global.security.jwt;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.kimje.chat.global.util.CookieUtil;


import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;

@Component

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtTokenProvider jwtTokenProvider;
	
	public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}


	@Override
	public boolean beforeHandshake(@NonNull ServerHttpRequest request,@NonNull ServerHttpResponse response,@NonNull WebSocketHandler wsHandler,
		@NonNull Map<String, Object> attributes) throws Exception {

		if(request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
			String token = CookieUtil.getCookie(httpServletRequest, "accessToken");
			if(token != null && jwtTokenProvider.validateToken(token)){
				Long userId = jwtTokenProvider.getUserIdFromToken(token);
				attributes.put("userId", userId);
				return true;
			}
		}

		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {
		

		// 로깅이나 에러처리 시 추후 사용
	}
}
