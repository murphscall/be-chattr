package com.kimje.chat.emailauth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class EmailRequestDTO {

	@Getter
	@Setter
	public static class Send {
		@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" , message = "이메일 형식이 올바르지 않습니다.")
		private String email;
	}

	@Getter
	@Setter
	public static class Verify {
		private String email;
		private String code;
	}
}
