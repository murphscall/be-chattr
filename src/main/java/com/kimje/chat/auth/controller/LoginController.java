package com.kimje.chat.auth.controller;


import com.kimje.chat.auth.dto.LoginDTO;
import com.kimje.chat.auth.service.LoginService;
import com.kimje.chat.global.response.ApiResponse;
import com.kimje.chat.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

  private final LoginService loginService;

  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO.Request dto){

    LoginDTO.Response loginResponse = loginService.login(dto);


    return ResponseEntity.ok().body(ApiResponse.success(loginResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<?>> logout(){
    return null;
  }


}
