package com.kimje.chat.chats.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kimje.chat.chats.service.MessageService;
import com.kimje.chat.global.security.OAuth2.AuthUser;

@RestController
public class MessageControler {

	private final MessageService messageService;

	public MessageControler(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping("/api/chats/{chatId}/msg")
	public String sendMessage(@PathVariable Long chatId) {
		return "sendMessage";
	}

	@DeleteMapping("/api/chats/{chatId}/msg/{messageId}")
	public String deleteMessage(@PathVariable Long chatId, @PathVariable Long messageId) {
		return "deleteMessage";
	}

	@PostMapping("/api/chats/{chatId}/msg/{msgId}/likes")
	public String likeMessage(@PathVariable Long chatId, @PathVariable Long msgId , @AuthenticationPrincipal AuthUser authUser) {
		messageService.addLike(chatId, msgId , authUser.getUserId());
		return "likeMessage";
	}

}
