package com.kimje.chat.chats.dto;

import com.kimje.chat.chats.enums.ChatRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatResponseDTO {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ChatInfo {
		private Long userId;
		private String name;
		private ChatRole role;

		public ChatInfo(Long userId, String name, ChatRole role) {
			this.userId = userId;
			this.name = name;
			this.role = role;
		}

	}

}
