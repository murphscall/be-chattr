package com.kimje.chat.emailauth.controller;

import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.emailauth.dto.EmailRequestDTO;
import com.kimje.chat.emailauth.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  private final EmailService emailService;

  public EmailController(EmailService emailService) {
    this.emailService = emailService;
  }

  @PostMapping("/api/email/send")
  public ResponseEntity<ApiResponse<?>> sendEmail(@RequestBody EmailRequestDTO.Send dto) throws MessagingException {
    System.out.println(dto.getEmail());
    emailService.sendEmail(dto);
    return ResponseEntity.ok().body(ApiResponse.success("인증 코드가 전송 되었습니다."));
  }

  @PostMapping("/api/email/verify")
  public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestBody EmailRequestDTO.Verify dto) throws MessagingException {

    emailService.verifyCode(dto);
    return ResponseEntity.ok().body(ApiResponse.success("이메일 인증 완료"));
  }

}
