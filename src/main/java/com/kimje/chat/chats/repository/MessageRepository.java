package com.kimje.chat.chats.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;

public interface MessageRepository extends JpaRepository<Message,Long>{



	@Query("SELECT DISTINCT m FROM Message m LEFT JOIN FETCH m.likes WHERE m.chatId.id = :chatId AND m.createdAt > :joinedAt")
	List<Message> findMessagesWithLikes(
		@Param("chatId") Long chatId,
		@Param("joinedAt") LocalDateTime joinedAt
	);

	Optional<Message> findByChatIdAndId(Chat chatId, long id);
}
