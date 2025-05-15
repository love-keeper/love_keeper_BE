package com.example.lovekeeper.domain.letter.service.query;

import java.time.LocalDate;

import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;

public interface LetterQueryService {

	/**
	 * 편지 리스트 조회
	 */
	LetterResponse.LetterListResponse getLetters(Long memberId, int page, int size);

	Long getLetterCount(Long memberId);

	LetterResponse.LetterDetailResponse getLetterDetail(Long memberId, Long letterId);

	/**
	 * 특정 날짜 편지 리스트 조회
	 */
	LetterResponse.LetterListResponse getLettersByDate(Long memberId, LocalDate date, int page, int size);
}
