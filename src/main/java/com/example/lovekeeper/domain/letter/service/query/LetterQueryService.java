package com.example.lovekeeper.domain.letter.service.query;

import java.time.Month;
import java.util.List;

import com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse;
import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;

public interface LetterQueryService {

	/**
	 * 편지 리스트 조회
	 */
	LetterResponse.LetterListResponse getLetters(Long memberId, int page, int size);

	List<LetterCountByDateResponse> getLetterCountByMonth(int year, Month month, Long memberId);

}
