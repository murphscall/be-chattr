package com.kimje.chat.chats.dto;

import java.time.LocalDateTime;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDTO {
	private Long chatId;
	private String content;
	private MessageType type;

	public Message toEntity(Chat chat, User user ){
		return Message.builder()
			.content(content)
			.type(type)
			.chatId(chat)
			.senderUserId(user)
			.isDeleted(false)
			.build();
	}
}
