package com.kimje.chat.chats.dto;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.enums.ChatRole;
import com.kimje.chat.chats.enums.ChatTopic;
import com.kimje.chat.chats.enums.ChatType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatRequestDTO {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Create {

		@NotBlank(message = "제목은 필수입니다.")
		@Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
		private String title;
		private String description;
		@NotNull(message = "주제를 선택하세요.")
		private ChatTopic topic;

		public Chat toEntity() {
			return Chat.builder()
				.title(this.title)
				.description(this.description)
				.topic(this.topic)
				.build();
		}
	}

	@Getter
	@Setter
	public static class ChangeRole {
		private ChatRole role;
	}

}
