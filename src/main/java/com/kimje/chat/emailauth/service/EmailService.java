package com.kimje.chat.emailauth.service;

import com.kimje.chat.global.exception.customexception.EmailCodeInvalidException;
import com.kimje.chat.global.exception.customexception.EmailCodeExpiredException;
import com.kimje.chat.global.redis.RedisService;
import com.kimje.chat.global.util.EmailVerifyPassGenerator;
import com.kimje.chat.emailauth.dto.EmailRequestDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.TimeUnit;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final RedisService redisService;
	private final JavaMailSender mailSender;
	private final EmailVerifyPassGenerator emailVerifyPassGenerator;

	public EmailService(JavaMailSender mailSender,
		EmailVerifyPassGenerator emailVerifyPassGenerator,
		RedisService redisService) {
		this.mailSender = mailSender;
		this.emailVerifyPassGenerator = emailVerifyPassGenerator;
		this.redisService = redisService;
	}

	public void sendEmail(EmailRequestDTO.Send dto) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		String code = emailVerifyPassGenerator.generateCode();

		helper.setTo(dto.getEmail());
		helper.setSubject("(실시간 채팅 Chattr) 이메일 인증 번호 발급");
		String content =
			"<html><head></head><body>"
				+ "<h2>이메일 인증 번호</h2>"
				+ "<p><strong>인증 번호 : </strong>"
				+ code
				+ "</p>"
				+ "</body></html>";

		helper.setText(content, true);
		redisService.set(dto.getEmail(), code, 5, TimeUnit.MINUTES);
		mailSender.send(message);
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

