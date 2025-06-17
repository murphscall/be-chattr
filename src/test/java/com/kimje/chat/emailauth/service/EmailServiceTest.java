package com.kimje.chat.emailauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.kimje.chat.emailauth.dto.EmailRequestDTO;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.util.EmailVerifyPassGenerator;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@InjectMocks
	EmailService emailService;

	@Mock
	RedisService redisService;

	@Mock
	JavaMailSender javaMailSender;

	@Mock
	EmailVerifyPassGenerator emailVerifyPassGenerator;

	@Test
	@DisplayName("인증 코드 저장 및 이메일 전송")
	void sendEmail() throws MessagingException {
		// Given
		EmailRequestDTO.Send dto = new EmailRequestDTO.Send();
		dto.setEmail("test123@gmail.com");

		// MimeMessage 생성
		MimeMessage mockMessage = new MimeMessage((Session) null);
		when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

		// generateCode() Mock 설정
		when(emailVerifyPassGenerator.generateCode()).thenReturn("123456");

		// When
		emailService.sendEmail(dto);

		// Then
		verify(emailVerifyPassGenerator).generateCode();
		verify(redisService).set("test123@gmail.com", "123456", 5L, TimeUnit.MINUTES);
		verify(javaMailSender).send(any(MimeMessage.class));
	}

	@Test
	@DisplayName("이메일 인증 여부 Redis 저장")
	void verifyCode() throws MessagingException {
		// given
		EmailRequestDTO.Verify dto = new EmailRequestDTO.Verify();
		dto.setEmail("test123@gmail.com");
		dto.setCode("111111");

		when(redisService.get(dto.getEmail())).thenReturn("111111");
		emailService.verifyCode(dto);

		verify(redisService).delete(dto.getEmail());
		verify(redisService).set(dto.getEmail(),"true",30,TimeUnit.MINUTES);

	}
}