package com.kimje.chat.emailauth.service;

import com.kimje.chat.emailauth.exception.EmailCodeInvalidException;
import com.kimje.chat.emailauth.exception.EmailCodeExpiredException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.util.EmailVerifyPassGenerator;
import com.kimje.chat.emailauth.dto.EmailRequestDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
	private final RedisService redisService;
	private final JavaMailSender mailSender;
	private final EmailVerifyPassGenerator emailVerifyPassGenerator;
	private final EmailSander emailSander;

	public EmailService(
		EmailVerifyPassGenerator emailVerifyPassGenerator,
		RedisService redisService, JavaMailSender mailSender , EmailSander emailSander) {

		this.emailVerifyPassGenerator = emailVerifyPassGenerator;
		this.redisService = redisService;
		this.mailSender = mailSender;
		this.emailSander = emailSander;
	}

	public void sendEmail(EmailRequestDTO.Send dto) throws MessagingException {
		String code = emailVerifyPassGenerator.generateCode();
		log.info("===== Redis 저장 시작 =====");
		redisService.set(dto.getEmail(), code, 5, TimeUnit.MINUTES);
		log.info("===== Redis 저장 완료 =====");

		log.info("===== Gmail 발송 시작 =====");

		emailSander.send(dto.getEmail(), code);

	}

	public void verifyCode(EmailRequestDTO.Verify dto) throws MessagingException {
		String storedCode = redisService.get(dto.getEmail());

		if (storedCode == null) {
			// 인증 코드 만료
			throw new EmailCodeExpiredException("인증 시간이 초과되었습니다.");
		}
		if (!storedCode.equals(dto.getCode())) {
			throw new EmailCodeInvalidException("잘못된 인증 코드 입니다.");
		}

		redisService.delete(dto.getEmail());
		redisService.set(dto.getEmail(), "true", 30, TimeUnit.MINUTES);

	}

}

