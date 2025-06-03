package com.kimje.chat.chats.service.message;

import org.springframework.stereotype.Service;

import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.entity.MessageLike;
import com.kimje.chat.chats.repository.MessageLikeRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.user.entity.User;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageCommandService {

	private final MessageRepository messageRepository;
	private final MessageLikeRepository messageLikeRepository;
	private final EntityManager em;

	public synchronized void addLike(Long chatId , Long msgId , Long userId) {

		Message message = messageRepository.findByChatIdAndId(em.getReference(Chat.class, chatId),msgId)
			.orElseThrow(() -> new IllegalStateException("이 채팅방에 없는 메시지 입니다."));

		User user = em.getReference(User.class, userId);

		MessageLike msgLike = new MessageLike();
		msgLike.setMessageId(message);
		msgLike.setUserId(user);

		messageLikeRepository.save(msgLike);
	}
}
