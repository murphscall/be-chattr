package com.kimje.chat.chats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kimje.chat.chats.entity.MessageLike;

@Repository
public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {
}
