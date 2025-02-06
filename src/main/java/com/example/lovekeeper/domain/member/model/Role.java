package com.example.lovekeeper.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

	ROLE_ADMIN("ROLE_ADMIN", "관리자"),
	ROLE_USER("ROLE_USER", "사용자");

	private final String key;
	private final String description;

}
