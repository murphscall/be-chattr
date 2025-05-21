package com.kimje.chat.chats.dto;

import java.time.LocalDateTime;

import com.kimje.chat.chats.enums.MessageType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDTO {
	private Long chatId;
	private String content;
	private MessageType type;
}
