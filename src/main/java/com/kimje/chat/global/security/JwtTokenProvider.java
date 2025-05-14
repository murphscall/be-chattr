package com.kimje.chat.global.security;

import com.kimje.chat.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKeyString;

  @Value("${jwt.access-token-validity-in-ms}")
  private long tokenValidityInMilliseconds;

  private Key secretKey;

  @PostConstruct
  protected void init() {
    this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
  }

  // 토큰 생성
  public String createToken(String userId, UserRole role) {
    Claims claims = Jwts.claims().setSubject(userId);
    claims.put("roles", role.name());

    Date now = new Date();
    Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // 인증 객체 추출
  public Authentication getAuthentication(String token) {
    Claims claims = getClaims(token);
    String userId = claims.getSubject();
    String role = (String) claims.get("role");

    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

    return new UsernamePasswordAuthenticationToken(userId, "", List.of(authority));
  }

  // 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // Claims 파싱
  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // 헤더에서 토큰 추출
  public String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}
