package com.kimje.chat.chats.entity;

import com.kimje.chat.chats.enums.NotificationType;
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
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	@Column(length = 500)
	private String content;
	@Column(columnDefinition = "boolean default false")
	private boolean isRead;

	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "chat_id")
	private Chat chatId;

	@ManyToOne
	@JoinColumn(name = "receiver_user_id")
	private User receiverUserId;

	@ManyToOne
	@JoinColumn(name = "message_id")
	private Message messageId;

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}

}
