package com.kimje.chat.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimje.chat.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ApiResponse<?> errorResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED, null , "액세스 토큰 유효하지 않음");
		ObjectMapper objectMapper = new ObjectMapper();
		String body = objectMapper.writeValueAsString(errorResponse);

		response.getWriter().write(body);
	}
}
