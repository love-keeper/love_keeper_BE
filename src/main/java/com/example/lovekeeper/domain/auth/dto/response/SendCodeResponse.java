package com.example.lovekeeper.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeResponse {

	private String code;

	public static SendCodeResponse of(String code) {
		return SendCodeResponse.builder()
			.code(code)
			.build();
	}
	
}
