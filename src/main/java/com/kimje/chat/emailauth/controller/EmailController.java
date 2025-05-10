package com.kimje.chat.emailauth.controller;

import com.kimje.chat.common.response.ApiResponse;
import com.kimje.chat.emailauth.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  private final EmailService emailService;

  public EmailController(EmailService emailService) {
    this.emailService = emailService;
  }

  @PostMapping("/api/email/send")
  public ResponseEntity<ApiResponse<?>> sendEmail(String email) throws MessagingException {
    emailService.sendEmail(email);
    return ResponseEntity.ok().body(ApiResponse.success("인증 코드가 전송 되었습니다."));
  }

  @PostMapping("/api/email/verify")
  public ResponseEntity<ApiResponse<?>> verifyEmail(String email) throws MessagingException {
    return null;
  }

}
