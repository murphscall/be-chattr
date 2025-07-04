package com.kimje.chat.chats.repository;

import com.kimje.chat.chats.dto.ChatResponseDTO;
import com.kimje.chat.chats.entity.Chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chat, Long> {

	@Query(
		value = """
        SELECT new com.kimje.chat.chats.dto.ChatResponseDTO$ChatInfo(
            c.id, c.title, c.description, c.topic, COUNT(cu), c.createdAt)
        FROM Chat c
        LEFT JOIN c.chatUsers cu
        GROUP BY c.id, c.title, c.description, c.topic, c.createdAt
        ORDER BY c.createdAt DESC
        """,
		countQuery = "SELECT COUNT(c) FROM Chat c"
	)
	Page<ChatResponseDTO.ChatInfo> getChats(Pageable pageable);


	@Query(value = """
    SELECT new com.kimje.chat.chats.dto.ChatResponseDTO$ChatInfo(
        c.id,
        c.title,
        c.description,
        c.topic,
        COUNT(cu),
        c.createdAt)
    FROM Chat c
    JOIN c.chatUsers cu
    GROUP BY c.id, c.title, c.description, c.topic, c.createdAt
    HAVING COUNT(cu) >= 3
    ORDER BY COUNT(cu) DESC
    """)
	Page<ChatResponseDTO.ChatInfo> findHotChats(Pageable pageable);

}
