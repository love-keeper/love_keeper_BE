package com.example.lovekeeper.domain.member.dto.response;

import java.time.LocalDate;

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
@Schema(description = "생일 변경 응답 DTO")
public class ChangeBirthdayResponse {

	@Schema(description = "변경된 생일 (yyyy-MM-dd)", example = "1999-01-01", required = true)
	private LocalDate birthday;

	public static ChangeBirthdayResponse of(LocalDate birthday) {
		return ChangeBirthdayResponse.builder()
			.birthday(birthday)
			.build();
	}
}