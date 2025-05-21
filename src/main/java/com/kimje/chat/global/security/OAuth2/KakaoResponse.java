package com.kimje.chat.global.security.OAuth2;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

	private Map<String, Object> attributes;

	public KakaoResponse(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		return (String)kakaoAccount.get("email");
	}

	@Override
	public String getName() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		if (kakaoAccount != null) {
			Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
			if (profile != null) {
				return (String)profile.get("nickname");
			}
		}
		// 이름이 없는 경우 대체 값 반환
		return "KakaoUser";
	}
}
