package com.kimje.chat.chats.dto;

import java.time.LocalDateTime;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.enums.ChatTopic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatResponseDTO {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChatInfo {
		private Long chatId;
		private String title;
		private String description;
		private ChatTopic topic;
		private Long memberCount;
		private LocalDateTime createdAt;



		public static ChatInfo from(Chat chat){
			return ChatInfo.builder()
				.chatId(chat.getId())
				.title(chat.getTitle())
				.description(chat.getDescription())
				.topic(chat.getTopic())
				.createdAt(chat.getCreatedAt())
				.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ChatUserInfo {
		private Long userId;
		private String name;
		private ChatRole role;

		public ChatUserInfo(Long userId, String name, ChatRole role) {
			this.userId = userId;
			this.name = name;
			this.role = role;
		}

	}

}
