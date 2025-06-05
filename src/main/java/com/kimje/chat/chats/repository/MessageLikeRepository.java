package com.kimje.chat.chats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kimje.chat.chats.entity.MessageLike;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {
    @Query("""
    SELECT ml.messageId.id
    FROM MessageLike ml
    WHERE ml.userId.id = :userId
      AND ml.messageId.chatId.id = :chatId
      AND ml.messageId.createdAt > :joinedAt
""")
    Set<Long> findLikedMessageIdsByUserIdAndChatIdAfter(
            @Param("userId") Long userId,
            @Param("chatId") Long chatId,
            @Param("joinedAt") LocalDateTime joinedAt
    );
}
