package com.kimje.chat.chats.entity;

import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(length = 1000)
	private String content;
	@Enumerated(EnumType.STRING)
	private MessageType type;
	@Column(columnDefinition = "boolean default false")
	private boolean isDeleted;
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "sender_user_id")
	private User senderUserId;
	@ManyToOne
	@JoinColumn(name = "chat_id")
	private Chat chatId;

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}

}
