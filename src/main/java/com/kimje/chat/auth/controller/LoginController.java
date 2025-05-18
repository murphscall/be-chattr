package com.kimje.chat.auth.controller;


import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.auth.dto.RefreshTokenRequest;
import com.kimje.chat.auth.service.LoginService;
import com.kimje.chat.auth.service.TokenService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.global.util.CookieUtil;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

  private final LoginService loginService;
  private final TokenService refeshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  public LoginController(LoginService loginService , TokenService refreshTokenService,
      JwtTokenProvider jwtTokenProvider) {
    this.loginService = loginService;
    this.refeshTokenService = refreshTokenService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO.Request dto , HttpServletResponse response){

    
    UserResponseDTO.Info loginResponse = loginService.login(dto ,response);

    // 유저정보와 액세스 토큰은 반환하고 refresh 토큰은 쿠키로 전송
    return ResponseEntity.ok().body(ApiResponse.success(loginResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<?>> logout(){
    return null;
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> createRefreshToken(@RequestBody RefreshTokenRequest dto,
      HttpServletRequest request,
      HttpServletResponse response){

    String refreshToken = CookieUtil.getCookie(request , "refreshToken");

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Refresh token not found"));
    }

    long userId = Long.parseLong(dto.getUserId());
    boolean isValid = refeshTokenService.validateRefreshToken( userId , refreshToken);

    if(!isValid){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid refresh token"));
    }

    String newAccessToken = jwtTokenProvider.createToken(userId , UserRole.ROLE_USER);
    return ResponseEntity.ok(Map.of("access_token", newAccessToken));
  }
}
