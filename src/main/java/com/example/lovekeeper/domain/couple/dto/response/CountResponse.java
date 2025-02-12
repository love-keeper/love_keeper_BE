package com.example.lovekeeper.domain.couple.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CountResponse {

	private LetterCountResponse letterCount;
	private PromiseCountResponse promiseCount;

	public static CountResponse of(LetterCountResponse letterCount,
		PromiseCountResponse promiseCount) {
		return CountResponse.builder()
			.letterCount(letterCount)
			.promiseCount(promiseCount)
			.build();
	}
}
