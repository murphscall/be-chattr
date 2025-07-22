package com.kimje.chat.user.dto;

import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;

import lombok.*;

public class UserRequestDTO {
	@Setter
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Create {
		@Schema(description = "사용자 이메일 주소", example = "test@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
		@Pattern(regexp = "^[a-zA-Z0-9_+&*-]{1,30}@[A-Za-z0-9-]{1,20}\\.[A-Za-z]{2,10}$",
			message = "이메일 : 이메일 형식을 올바르게 입력해주세요.")
		private String email;

		@Schema(description = "사용자 비밀번호" , example = "test1234^^" , requiredMode = Schema.RequiredMode.REQUIRED)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,20}$",
			message = "비밀번호 : 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
		private String password;

		@Schema(description = "사용자 핸드폰 번호" , example = "010-1516-1551" , requiredMode = Schema.RequiredMode.REQUIRED)
		@Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "핸드폰 : 핸드폰 번호를 올바르게 입력해주세요.")
		private String phone;

		@Schema(description = "사용자 별명" , example = "홍길덩" , requiredMode = Schema.RequiredMode.REQUIRED)
		@Pattern(regexp = "^[가-힣]{3,5}$", message = "이름 : 한글 3~5자로 입력해주세요.")
		private String name;

		public User toEntity() {
			return User.builder()
				.email(email)
				.phone(phone)
				.name(name)
				.role(UserRole.ROLE_USER)
				.userLogins(new ArrayList<>())
				.build();
		}

	}

	@Setter
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Update {
		private String password;
		private String phone;
		private String name;
	}

	@Setter
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Delete {
		@Schema(description = "회원의 현재 비밀번호" , example = "abc1234^^", requiredMode = Schema.RequiredMode.REQUIRED)
		private String password;
	}

}
