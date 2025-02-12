package com.example.lovekeeper.domain.letter.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LetterCountByDateResponse {
	private LocalDate date;       // 날짜
	private int receivedCount;    // 받은 편지 수
	private int sentCount;        // 보낸 편지 수
	private int totalCount;       // 총 편지 수 (보낸 + 받은)

	// Query 결과를 받기 위한 생성자 추가
	public LetterCountByDateResponse(LocalDate date, Long count) {
		this.date = date;
		this.receivedCount = count.intValue();
		this.sentCount = 0;
		this.totalCount = count.intValue();
	}
}