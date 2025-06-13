package com.kimje.chat.chats.exception;

public class ChatBanAccessDeniedException extends RuntimeException {
    public ChatBanAccessDeniedException(String message) {
        super(message);
    }
}
