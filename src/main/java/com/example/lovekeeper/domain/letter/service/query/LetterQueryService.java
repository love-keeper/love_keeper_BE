package com.example.lovekeeper.domain.letter.service.query;

import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;

public interface LetterQueryService {

	/**
	 * 편지 리스트 조회
	 */
	LetterResponse.LetterListResponse getLetters(Long memberId, int page, int size);

}
