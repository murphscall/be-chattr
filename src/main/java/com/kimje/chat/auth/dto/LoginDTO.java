package com.kimje.chat.auth.dto;

import com.kimje.chat.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoginDTO {

  @Getter
  @Setter
  public static class Request {
    public String email;
    public String password;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    public String accessToken;
    public UserResponseDTO.Info userInfo;

  }

}
