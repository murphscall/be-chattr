package com.kimje.chat.global.exception.exhandler.chat;

import com.kimje.chat.chats.exception.ChatBanAccessDeniedException;
import com.kimje.chat.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatBanAccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handlerChatBanAccessDeniedException(final ChatBanAccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(HttpStatus.FORBIDDEN,null, e.getMessage()));
    }


}
