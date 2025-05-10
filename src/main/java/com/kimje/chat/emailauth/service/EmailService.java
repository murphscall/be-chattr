package com.kimje.chat.emailauth.service;

import com.kimje.chat.common.util.EmailVerifyPassGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final EmailVerifyPassGenerator emailVerifyPassGenerator;
  private final RedisTemplate<String, String> redisTemplate;

  public EmailService(JavaMailSender mailSender,
      EmailVerifyPassGenerator emailVerifyPassGenerator,
      RedisTemplate<String, String> redisTemplate) {
    this.mailSender = mailSender;
    this.emailVerifyPassGenerator = emailVerifyPassGenerator;
    this.redisTemplate = redisTemplate;
  }


  public void sendEmail(String email) throws MessagingException {
    if(!isValidEmail(email)) {
      throw new MessagingException("이메일 주소를 확인해주세요.");
    }

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    String code = emailVerifyPassGenerator.generateCode();

    helper.setTo(email);
    helper.setSubject("(실시간 채팅 Chattr) 이메일 인증 번호 발급");
    String content =
        "<html><head></head><body>"
            + "<h2>이메일 인증 번호</h2>"
            + "<p><strong>인증 번호 : </strong>"
            + code
            + "</p>"
            + "</body></html>";

    helper.setText(content, true);
    redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
    mailSender.send(message);
  }

  public void verifyCode(String email , String code){
    String storedCode = redisTemplate.opsForValue().get(email);
    if(!storedCode.equals(code)) {
      // 잘못된 인증 코드
    }
    if(storedCode == null){
      // 인증 코드 만료
    }
  }

  private boolean isValidEmail(String email) {
    return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
  }
}

