package com.example.lovekeeper.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 인증 코드 발송 응답 DTO")
public class SendCodeResponse {

	@Schema(description = "발송된 인증 코드 (보안상 클라이언트에 노출되지 않을 수 있음)", example = "123456", nullable = true)
	private String code;

	public static SendCodeResponse of(String code) {
		return SendCodeResponse.builder()
			.code(code)
			.build();
	}
}