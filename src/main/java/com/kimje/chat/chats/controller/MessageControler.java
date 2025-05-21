package com.kimje.chat.chats.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageControler {

	@PostMapping("/api/chats/{chatId}/msg")
	public String sendMessage(@PathVariable Long chatId) {
		return "sendMessage";
	}

	@DeleteMapping("/api/chats/{chatId}/msg/{messageId}")
	public String deleteMessage(@PathVariable Long chatId, @PathVariable Long messageId) {
		return "deleteMessage";
	}

	@PostMapping("/api/chats/{chatId}/msg/{msgId}/likes")
	public String likeMessage(@PathVariable Long chatId, @PathVariable Long msgId) {
		return "likeMessage";
	}

}
