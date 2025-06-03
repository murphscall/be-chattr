package com.kimje.chat.chats.entity;

import com.kimje.chat.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"})
)
public class MessageLike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "message_id")
	private Message messageId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User userId;

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}
