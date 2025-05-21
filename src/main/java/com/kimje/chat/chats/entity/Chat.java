package com.kimje.chat.chats.entity;

import com.kimje.chat.chats.enums.ChatTopic;
import com.kimje.chat.chats.enums.ChatType;
import com.kimje.chat.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String description;
	@Enumerated(EnumType.STRING)
	private ChatTopic topic;
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChatUser> chatUsers = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}

}
