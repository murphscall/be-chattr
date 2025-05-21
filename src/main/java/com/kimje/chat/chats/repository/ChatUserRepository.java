package com.kimje.chat.chats.repository;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
	int countByUserIdAndRole(Long userId, ChatRole role);

	List<ChatUser> chat(Chat chat);

	Optional<ChatUser> findByChatIdAndUserId(Long chatId, Long userId);

	int countByChatId(Long chatId);

	List<ChatUser> findAllByChatId(Long chatId);

	Optional<ChatUser> findFirstByChatIdAndRole(Long chatId, ChatRole chatRole);

}
