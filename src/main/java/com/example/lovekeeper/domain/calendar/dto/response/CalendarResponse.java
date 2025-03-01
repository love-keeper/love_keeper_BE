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

	@Schema(description = "해당 월의 총 편지 개수")
	private long totalLetterCount;

	@Schema(description = "해당 월의 총 약속 개수")
	private long totalPromiseCount;

	@Schema(description = "특정 날짜의 총 편지 개수")
	private long dailyLetterCount;

	@Schema(description = "특정 날짜의 총 약속 개수")
	private long dailyPromiseCount;

	public static CalendarResponse of(List<DateCountResponse> letters, List<DateCountResponse> promises,
		long totalLetterCount, long totalPromiseCount,
		long dailyLetterCount, long dailyPromiseCount) {
		return CalendarResponse.builder()
			.letters(letters)
			.promises(promises)
			.totalLetterCount(totalLetterCount)
			.totalPromiseCount(totalPromiseCount)
			.dailyLetterCount(dailyLetterCount)
			.dailyPromiseCount(dailyPromiseCount)
			.build();
	}
}