package com.kimje.chat.emailauth.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSander {

	private final JavaMailSender mailSender;

	@Async // 이 메서드는 이제 프록시를 통해 호출됩니다.
	public void send(String toEmail, String code) {
		log.info("===== (Async) Gmail 발송 시작: {} =====", toEmail);
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(toEmail);
			helper.setSubject("(실시간 채팅 Chattr) 이메일 인증 번호 발급");
			String content =
				"<html><head></head><body>"
					+ "<h2>이메일 인증 번호</h2>"
					+ "<p><strong>인증 번호 : </strong>"
					+ code
					+ "</p>"
					+ "</body></html>";

			helper.setText(content, true);

			mailSender.send(message);
			log.info("===== (Async) Gmail 발송 완료: {} =====", toEmail);
		} catch (MessagingException e) {
			log.error("🔴 [비동기 이메일 발송 실패] 받는 사람: {}, 에러: {}", toEmail, e.getMessage());
		}
	}
}

