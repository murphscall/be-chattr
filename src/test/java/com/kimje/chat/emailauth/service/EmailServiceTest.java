package com.kimje.chat.emailauth.service;

import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kimje.chat.emailauth.dto.EmailRequestDTO;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.util.EmailVerifyPassGenerator;

import jakarta.mail.MessagingException;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@InjectMocks
	EmailService emailService;

	@Mock
	RedisService redisService;

	// EmailService는 더 이상 JavaMailSender를 직접 사용하지 않으므로 Mock 객체가 필요 없습니다.
	// 대신 EmailSander를 Mocking 해야 합니다.
	@Mock
	EmailSander emailSander;

	@Mock
	EmailVerifyPassGenerator emailVerifyPassGenerator;

	@Test
	@DisplayName("인증 코드 저장 및 비동기 이메일 전송 요청")
	void sendEmail() throws MessagingException {
		// Given
		EmailRequestDTO.Send dto = new EmailRequestDTO.Send();
		dto.setEmail("test123@gmail.com");
		String generatedCode = "123456";

		// Mock 객체들의 행동을 정의합니다.
		when(emailVerifyPassGenerator.generateCode()).thenReturn(generatedCode);
		// emailSander.send()는 void를 반환하므로, 특별히 행동을 정의할 필요는 없습니다.
		// 호출 여부만 검증하면 됩니다.

		// When
		emailService.sendEmail(dto);

		// Then
		// 1. 인증 코드가 생성되었는지 검증합니다.
		verify(emailVerifyPassGenerator).generateCode();
		// 2. 생성된 코드가 Redis에 올바른 파라미터로 저장되었는지 검증합니다.
		verify(redisService).set("test123@gmail.com", generatedCode, 5L, TimeUnit.MINUTES);
		// 3. 비동기 이메일 발송기(EmailSander)가 올바른 파라미터로 호출되었는지 검증합니다.
		verify(emailSander).send("test123@gmail.com", generatedCode);
	}

	@Test
	@DisplayName("이메일 인증 여부 Redis 저장")
	void verifyCode() throws MessagingException {
		// given
		EmailRequestDTO.Verify dto = new EmailRequestDTO.Verify();
		dto.setEmail("test123@gmail.com");
		dto.setCode("111111");

		when(redisService.get(dto.getEmail())).thenReturn("111111");

		// when
		emailService.verifyCode(dto);

		// then
		verify(redisService).delete(dto.getEmail());
		verify(redisService).set(dto.getEmail(), "true", 30, TimeUnit.MINUTES);
	}
}