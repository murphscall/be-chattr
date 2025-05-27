package com.kimje.chat.chats.dto;

import java.time.LocalDateTime;

import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDTO {
	private Long id;
	private Long chatId;
	private Long senderId;
	private String senderName;
	private String content;
	private MessageType type;
	private boolean isDeleted;
	private Long targetId;
	private LocalDateTime createdAt;

	/** Message → DTO 변환 (null 안전) */
	public static MessageResponseDTO of(Message m) {
		return MessageResponseDTO.builder()
			.id(m.getId())
			.chatId(m.getChatId().getId())
			.senderId(m.getSenderUserId() != null ? m.getSenderUserId().getId() : null)
			.senderName(m.getSenderUserId() != null ? m.getSenderUserId().getName() : "SYSTEM")
			.content(m.getContent())
			.type(m.getType())
			.isDeleted(m.isDeleted())
			.createdAt(m.getCreatedAt())
			.build();
	}
	public static MessageResponseDTO of(Message m, Long targetId) {
		return MessageResponseDTO.builder()
			.id(m.getId())
			.chatId(m.getChatId().getId())
			.senderId(m.getSenderUserId() != null ? m.getSenderUserId().getId() : null)
			.senderName(m.getSenderUserId() != null ? m.getSenderUserId().getName() : "SYSTEM")
			.content(m.getContent())
			.type(m.getType())
			.isDeleted(m.isDeleted())
			.createdAt(m.getCreatedAt())
			.targetId(targetId) // ✅ 추가
			.build();
	}

	/** from() 은 of() 를 그대로 위임하도록 통일 */
	public static MessageResponseDTO from(Message m) {
		return of(m);
	}
}
