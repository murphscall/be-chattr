package com.kimje.chat.emailauth.dto;

import lombok.Getter;
import lombok.Setter;

public class EmailRequestDTO {

	@Getter
	@Setter
	public static class Send {
		private String email;
	}

	@Getter
	@Setter
	public static class Verify {
		private String email;
		private String code;
	}
}
