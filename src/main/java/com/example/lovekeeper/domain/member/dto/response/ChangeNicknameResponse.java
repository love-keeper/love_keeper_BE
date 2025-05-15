package com.example.lovekeeper.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Schema(description = "닉네임 변경 응답 DTO")
public class ChangeNicknameResponse {

	@Schema(description = "변경된 닉네임", example = "newlover", required = true)
	private String nickname;

	public static ChangeNicknameResponse of(String nickname) {
		return ChangeNicknameResponse.builder()
			.nickname(nickname)
			.build();
	}
}