package com.kimje.chat.chats.service.message;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.Message;
import com.kimje.chat.chats.entity.MessageLike;
import com.kimje.chat.chats.enums.MessageType;
import com.kimje.chat.chats.repository.ChatRoomRepository;
import com.kimje.chat.chats.repository.ChatUserRepository;
import com.kimje.chat.chats.repository.MessageLikeRepository;
import com.kimje.chat.chats.repository.MessageRepository;
import com.kimje.chat.user.entity.User;
import com.kimje.chat.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandService {

	private final MessageRepository messageRepository;
	private final MessageLikeRepository messageLikeRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatRoomRepository chatRepository;
	private final EntityManager em;
	private final UserRepository userRepository;



	public Message saveUserMessage(Long chatId, Long senderId, String content, MessageType type) {
		// 1. 세션에서 userId 추출

		User sender = userRepository.findById(senderId)
			.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다. ID: " + senderId));

		Chat chat = chatRepository.findById(chatId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅방입니다. ID: " + chatId));

		if (chatUserRepository.findByChatIdAndUserId(chatId, senderId).isEmpty()) {
			throw new AccessDeniedException("이 채팅방에 참여 중인 사용자가 아닙니다.");
		}


		Message message = Message.builder()
			.chatId(chat)
			.senderUserId(sender)
			.content(content)
			.type(type)
			.isDeleted(false)
			.build();

		return messageRepository.save(message);

	}

	public Message saveSystemMessage(Long chatId, String content, MessageType type) {

		// 1. 채팅방 존재 여부 확인 (필수 검증)
		Chat chat = chatRepository.findById(chatId)
			.orElseThrow(() -> new IllegalStateException("존재하지 않는 채팅방에 시스템 메시지를 보낼 수 없습니다. ID: " + chatId));

		// 2. 검증 통과 후 메시지 생성 및 저장
		Message message = Message.builder()
			.chatId(chat)
			.senderUserId(null) // 시스템 메시지는 발신자가 없음
			.content(content)
			.type(type)
			.isDeleted(false)
			.build();

		return messageRepository.save(message);
	}


	public synchronized void toggleLike(Long chatId , Long msgId , Long userId) {

		Message message = messageRepository.findByChatIdAndId(em.getReference(Chat.class, chatId),msgId)
			.orElseThrow(() -> new IllegalStateException("이 채팅방에 없는 메시지 입니다."));

		User user = em.getReference(User.class, userId);

		Optional<MessageLike> existingLike = messageLikeRepository
			.findByMessageIdAndUserId(message, user);

		if(existingLike.isPresent()) {
			messageLikeRepository.delete(existingLike.get());
		}else{
			MessageLike msgLike = new MessageLike();
			msgLike.setMessageId(message);
			msgLike.setUserId(user);
			messageLikeRepository.save(msgLike);
		}
	}
}
