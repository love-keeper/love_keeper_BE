package com.example.lovekeeper.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChangeNicknameResponse {

	private String nickname;

	public static ChangeNicknameResponse of(String nickname) {
		return ChangeNicknameResponse.builder()
			.nickname(nickname)
			.build();
	}
}
