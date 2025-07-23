package com.kimje.chat.global.security.OAuth2;

import com.kimje.chat.auth.service.TokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {


	private final TokenService tokenService;

	public OAuth2LoginSuccessHandler(TokenService tokenService) {
		this.tokenService = tokenService;

	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();

		tokenService.createAccessToken(oAuth2User.getUserId(), oAuth2User.getRole(), response);
		tokenService.createAndSaveRefreshToken(oAuth2User.getUserId(), response);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.sendRedirect("http://localhost:5173");

	}
}
