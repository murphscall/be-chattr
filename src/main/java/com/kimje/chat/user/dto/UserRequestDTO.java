package com.kimje.chat.user.dto;

import com.kimje.chat.user.entity.Users;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


public class UserRequestDTO {
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create{
        @Pattern(regexp = "^[a-zA-Z0-9_+&*-]{1,30}@[A-Za-z0-9-]{1,20}\\.[A-Za-z]{2,10}$",
                message = "이메일 : 이메일 형식을 올바르게 입력해주세요.")
        private String email;

        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,20}$",
                message = "비밀번호 : 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotNull(message = "휴대폰 번호를 입력해주세요.")
        private String phone;

        @Pattern(regexp = "^[가-힣]{3,5}$", message = "이름 : 한글 3~5자로 입력해주세요.")
        private String name;

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
        private String password;
        private String phone;
        private String name;
    }
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Delete{
        private String password;
    }



}
