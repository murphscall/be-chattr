package com.kimje.chat.auth.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.user.dto.UserResponseDTO;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.entity.UserLogin;
import com.kimje.chat.user.enums.UserRole;
import com.kimje.chat.user.repository.UserLoginRepository;
import com.kimje.chat.user.repository.UserRepository;

@SpringBootTest
@Transactional
class AuthServiceIntegrationTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserLoginRepository userLoginRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private User testUser;
	private UserLogin testUserLogin;
	private final String TEST_EMAIL = "test@example.com";
	private final String TEST_PASSWORD = "password123";
	private final String TEST_NAME = "테스트유저";

	@BeforeEach
	void setUp() {

		// 🎯 1. User 테이블에 기본 정보 저장
		testUser = User.builder()
			.email(TEST_EMAIL)
			.name(TEST_NAME)
			.password(passwordEncoder.encode(TEST_PASSWORD))
			.role(UserRole.ROLE_USER)
			.build();

		testUser = userRepository.save(testUser);

		// 🎯 2. UserLogin 테이블에 로그인 정보 저장
		testUserLogin = UserLogin.builder()
			.user(testUser)  // User 테이블과 연결
			.loginType("KAKAO")
			.providerId("test@provider.com")
			.build();

		testUserLogin = userLoginRepository.save(testUserLogin);
	}

	@Test
	@DisplayName("통합테스트 - 로그인 성공")
	public void loginUser(){
		LoginDTO.Request loginRequest = new LoginDTO.Request();
		loginRequest.setEmail(TEST_EMAIL);
		loginRequest.setPassword(TEST_PASSWORD);

		MockHttpServletResponse response = new MockHttpServletResponse();
		// when
		UserResponseDTO.Info result = authService.loginUser(loginRequest, response);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
		assertThat(result.getName()).isEqualTo(TEST_NAME);
	}

}