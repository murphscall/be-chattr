package com.kimje.chat.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserResponseDTO {

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	public static class Info {
		private Long userId;
		private String email;
		private String name;
		private String phone;
		private LocalDateTime createdAt;

		public Info(Long userId, String email, String name, String phone, LocalDateTime createdAt) {
			this.userId = userId;
			this.email = email;
			this.name = name;
			this.phone = phone;
			this.createdAt = createdAt;
		}
	}

}
