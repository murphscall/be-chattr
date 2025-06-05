package com.kimje.chat.chats.entity;

import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.user.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(length = 1000)
	private String content;
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
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
	@OneToMany(mappedBy = "messageId", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessageLike> likes = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}

}
