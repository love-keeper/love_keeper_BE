package com.example.lovekeeper.domain.auth.dto.response;

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
@Schema(description = "변경된 생일 응답")
public class ChangeBirthdayResponse {

	@Schema(description = "변경된 생일", example = "1999-01-01")
	private LocalDate birthday;

	public static ChangeBirthdayResponse of(LocalDate birthday) {
		return ChangeBirthdayResponse.builder()
			.birthday(birthday)
			.build();
	}
}
