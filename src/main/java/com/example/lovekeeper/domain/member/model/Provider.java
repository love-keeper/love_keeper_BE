package com.example.lovekeeper.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {

	LOCAL("local", "로컬"),
	GOOGLE("google", "구글"),
	FACEBOOK("facebook", "페이스북"),
	KAKAO("kakao", "카카오"),
	NAVER("naver", "네이버"),
	APPLE("apple", "애플");

	private final String key;
	private final String description;
	
}
