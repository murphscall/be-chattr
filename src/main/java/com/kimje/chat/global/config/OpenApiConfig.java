package com.kimje.chat.global.config;

import java.util.List;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openApi() {
		Info info = new Info()
			.title("실시간 채팅 Chattr API Document")
			.version("1.0")
			.description("실시간 채팅 Chattr의 API 명세서 입니다.");

		// 원하는 순서대로 Tag 목록을 정의합니다.
		List<Tag> tags = List.of(
			new Tag().name("사용자 및 인증 API").description("회원가입, 로그인 등 사용자 인증 관련 API"),
			new Tag().name("채팅방 API").description("채팅방 생성, 목록 조회 등 채팅방 자체를 관리하는 API"),
			new Tag().name("채팅 참여 API").description("채팅방 참여, 나가기, 멤버 목록 조회 API"),
			new Tag().name("채팅 메시지 API").description("메시지 조회, 좋아요 관련 API"),
			new Tag().name("채팅 관리 API").description("방장의 유저 추방 및 권한 변경 API"),
			new Tag().name("애플리케이션 관리자 API").description("애플리케이션 기능..")
		);

		return new OpenAPI()
			.components(new Components())
			.info(info)
			.tags(tags);
	}
}
