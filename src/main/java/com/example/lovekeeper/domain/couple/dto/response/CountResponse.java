package com.example.lovekeeper.domain.couple.dto.response;

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
@Schema(description = "편지와 약속 개수 응답")
public class CountResponse {

	@Schema(description = "편지 개수 응답")
	private LetterCountResponse letterCount;

	@Schema(description = "약속 개수 응답")
	private PromiseCountResponse promiseCount;

	public static CountResponse of(LetterCountResponse letterCount,
		PromiseCountResponse promiseCount) {
		return CountResponse.builder()
			.letterCount(letterCount)
			.promiseCount(promiseCount)
			.build();
	}
}
