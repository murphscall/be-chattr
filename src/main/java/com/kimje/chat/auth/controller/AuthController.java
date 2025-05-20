package com.kimje.chat.auth.controller;


import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.auth.dto.RefreshTokenRequest;
import com.kimje.chat.auth.service.AuthService;
import com.kimje.chat.auth.service.TokenService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.OAuth2.AuthUser;
import com.kimje.chat.global.security.jwt.JwtTokenProvider;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;

  public AuthController(AuthService authService, TokenService tokenService) {
    this.authService = authService;
    this.tokenService = tokenService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO.Request dto , HttpServletResponse response){

    
    UserResponseDTO.Info loginResponse = authService.loginUser(dto ,response);

    // 유저정보와 액세스 토큰은 반환하고 refresh 토큰은 쿠키로 전송
    return ResponseEntity.ok().body(ApiResponse.success(loginResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal AuthUser authUser, HttpServletRequest request, HttpServletResponse response){

    authService.logoutUser(authUser,request,response);

    return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest dto,
      HttpServletRequest request,
      HttpServletResponse response){

    String refreshToken = CookieUtil.getCookie(request , "refreshToken");
    long userId = Long.parseLong(dto.getUserId());

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Refresh token not found"));
    }

    boolean isValid = tokenService.validateRefreshToken( userId , refreshToken);

    if(!isValid){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid refresh token"));
    }

    tokenService.createAccessToken(userId , UserRole.ROLE_USER , response );
    return ResponseEntity.ok().build();
  }
}
