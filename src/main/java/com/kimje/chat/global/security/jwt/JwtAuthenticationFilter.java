package com.kimje.chat.global.security.jwt;

import com.kimje.chat.global.exception.JwtInvalidTokenException;
import com.kimje.chat.global.exception.JwtTokenExpiredException;
import com.kimje.chat.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

//    String token = jwtTokenProvider.resolveToken(request);
    String token = CookieUtil.getCookie(request, "accessToken");

    try {
      if (token != null) {
        jwtTokenProvider.validateTokenOrThrow(token);  // 예외 던짐
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (JwtTokenExpiredException e) {
      sendErrorResponse(response, "ACCESS_TOKEN_EXPIRED");
      return;
    } catch (JwtInvalidTokenException e) {
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
