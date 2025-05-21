package com.kimje.chat.global.security.jwt;

import com.kimje.chat.global.exception.customexception.JwtInvalidTokenException;
import com.kimje.chat.global.exception.customexception.JwtTokenExpiredException;
import com.kimje.chat.global.security.CustomUserDetails;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

	private final UserRepository userRepository;
	@Value("${jwt.secret}")
	private String secretKeyString;

	@Value("${jwt.access-token-validity-in-ms}")
	private long tokenValidityInMilliseconds;

	private Key secretKey;

	public JwtTokenProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PostConstruct
	protected void init() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
	}

	// 토큰 생성
	public String createToken(long userId, UserRole role) {
		Claims claims = Jwts.claims().setSubject(Long.toString(userId));
		claims.put("role", role.name());

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject()); // subject == userId
	}

	// 인증 객체 추출
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		String userId = claims.getSubject();
		String role = (String)claims.get("role");

		System.out.println(userId + " " + role);

		User user = userRepository.findById(Long.parseLong(userId))
			.orElseThrow(() -> new UsernameNotFoundException("user not found"));

		CustomUserDetails userDetails = new CustomUserDetails(user);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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

	public void validateTokenOrThrow(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token); // 여기서 예외 발생 가능
		} catch (ExpiredJwtException e) {
			throw new JwtTokenExpiredException("Access token expired");
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtInvalidTokenException("Invalid access token");
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
