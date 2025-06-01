package com.kimje.chat.chats.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;

public interface MessageRepository extends JpaRepository<Message,Long>{


	List<Message> findByChatIdAndCreatedAtAfter(Chat chat,LocalDateTime joinUser);

	List<Message> findByChatId_IdAndCreatedAtAfter(Long chatId, LocalDateTime joinedAt);
}
