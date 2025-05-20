package com.kimje.chat.user.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserResponseDTO {

  @Getter
  @Setter
  @Builder
  public static class Info{
    private long userId;
    private String email;
    private String name;
    private String phone;
    private LocalDateTime createdAt;

  }

}
