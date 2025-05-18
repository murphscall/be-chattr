package com.kimje.chat.auth.service;

import com.kimje.chat.global.security.JwtTokenProvider;
import com.kimje.chat.user.enums.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final StringRedisTemplate redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  public TokenService(StringRedisTemplate redisTemplate, JwtTokenProvider jwtTokenProvider) {
    this.redisTemplate = redisTemplate;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public void createAccessToken(long userId, UserRole role, HttpServletResponse response) {
    String accessToken = jwtTokenProvider.createToken(userId , role);
    ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(60  * 15)
        .sameSite("Lax")
        .build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  public void createAndSaveRefreshToken(long userId , HttpServletResponse response) {
    String refreshToken = UUID.randomUUID().toString();

    redisTemplate.opsForValue().set("refresh:" + userId, refreshToken, Duration.ofDays(14));

    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(Duration.ofDays(14))
        .sameSite("Lax")
        .build();

    response.addHeader("Set-Cookie", cookie.toString());
  }

  public boolean validateRefreshToken(long userId, String refreshToken) {
    String storedToken = redisTemplate.opsForValue().get("refresh:" + userId);
    return refreshToken.equals(storedToken);
  }

  public void deleteRefreshToken(Long userId) {
    redisTemplate.delete("refresh:" + userId);
  }

}
