package com.kimje.chat.user.dto;

import lombok.Getter;
import lombok.Setter;

public class UserReponseDTO {

  @Getter
  @Setter
  public static class Info{
    private String email;
    private String name;
    private String phone;
    private String loginType;
    private String createdAt;
  }

}
