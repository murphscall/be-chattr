package com.kimje.chat.chats.repository;

import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.entity.Chat;
import com.kimje.chat.chats.entity.ChatUser;
import com.kimje.chat.chats.enums.ChatRole;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
	// 역할이 마스터인 유저가 만든 방의 수
	int countByUserIdAndRole(Long userId, ChatRole role);

	// chatId와 userId로 chat user 를 찾는 쿼리
	@Query("""
		    SELECT cu
		    FROM ChatUser cu
		    JOIN FETCH cu.user
		    JOIN FETCH cu.chat
		    WHERE cu.chat.id = :chatId AND cu.user.id = :userId
		""")
	Optional<ChatUser> findByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);
	boolean existsByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

	// chatId 를 가진 채팅방에 유저가 몇명인지 조회하는 쿼리
	int countByChatId(Long chatId);

	// chatId를 가진 채팅방 참여자 목록을 모두 조회하는 쿼리
	List<ChatUser> findAllByChatId(Long chatId);

	Optional<ChatUser> findFirstByChatIdAndRole(Long chatId, ChatRole chatRole);

	@Query("""
		SELECT new com.kimje.chat.chats.dto.ChatResponseDTO$ChatInfo(
		    c.id,
		    c.title,
		    c.description,
		    c.topic,
		    COUNT(cu2),
		    c.createdAt)
		FROM ChatUser cu
		JOIN cu.chat c
		JOIN c.chatUsers cu2
		WHERE cu.user.id = :userId
		GROUP BY c.id, c.title, c.description, c.topic, c.createdAt
		ORDER BY c.createdAt DESC""")
	Page<ChatResponseDTO.ChatInfo> findMyChatsWithCount(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT cu.chat FROM ChatUser cu WHERE cu.user.id = :userId")
	Page<Chat> findChatsByUserId(@Param("userId") Long userId, Pageable pageable);

	Long chat(Chat chat);

	@Modifying
	@Query("DELETE FROM ChatUser cu WHERE cu.user.id = :userId AND cu.chat.id = :chatId")
	void deleteByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);
}
