package com.example.lovekeeper.domain.couple.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "초대 코드 생성 응답")
public class GenerateCodeResponse {

	@Schema(description = "초대 코드", example = "ABCD1234")
	private String inviteCode;

	public static GenerateCodeResponse of(String inviteCode) {
		return GenerateCodeResponse.builder()
			.inviteCode(inviteCode)
			.build();
	}

}
