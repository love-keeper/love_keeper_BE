package com.example.lovekeeper.domain.calendar.dto.response;

import java.util.List;

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
@Schema(description = "캘린더 응답")
public class CalendarResponse {

	@Schema(description = "편지 목록")
	private List<DateCountResponse> letters;

	@Schema(description = "약속 목록")
	private List<DateCountResponse> promises;

	public static CalendarResponse of(List<DateCountResponse> letters, List<DateCountResponse> promises) {
		return CalendarResponse.builder()
			.letters(letters)
			.promises(promises)
			.build();
	}

}
