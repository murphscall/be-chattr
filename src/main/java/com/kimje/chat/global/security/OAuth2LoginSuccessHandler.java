package com.kimje.chat.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimje.chat.auth.service.TokenService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final UserService userService;

  public OAuth2LoginSuccessHandler(TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;

  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

    tokenService.createAccessToken(oAuth2User.getUserId(),oAuth2User.getRole() , response);
    tokenService.createAndSaveRefreshToken(oAuth2User.getUserId(), response);

    UserResponseDTO.Info userInfo = userService.getUserInfo(oAuth2User.getUserId());
    // accessToken만 포함된 Map 만들기
    Map<String, Object> responseData = Map.of("userInfo", userInfo);
    ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(responseData);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(apiResponse);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(json);

  }
}
