package com.kimje.chat.chats.dto;

import java.util.List;

import com.kimje.chat.global.response.PageResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAllResponseDTO {

	private PageResponse allChats;
	private PageResponse hotChats;
	private List<ChatResponseDTO.ChatInfo> myChats;
	private PageResponse meChats;

}
