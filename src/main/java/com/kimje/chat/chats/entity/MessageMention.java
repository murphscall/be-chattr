package com.kimje.chat.chats.entity;

import com.kimje.chat.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class MessageMention {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "message_id")
	private Message messageId;

	@ManyToOne
	@JoinColumn(name = "receiver_user_id")
	private User receiverUserId;

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}
