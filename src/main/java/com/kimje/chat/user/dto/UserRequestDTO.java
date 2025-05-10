package com.kimje.chat.user.dto;

import com.kimje.chat.user.entity.Users;
import lombok.*;


public class UserRequestDTO {
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create{
        public String email;
        public String password;
        public String phone;
        public String name;

        public Users toEntity(){
            return Users.builder()
                    .email(email)
                    .password(password)
                    .phone(phone)
                    .name(name)
                    .role("ROLE_USER")
                    .build();
        }

    }
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update{
        public String password;
        public String phone;
        public String name;
    }
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Delete{
        public String password;
    }



}
