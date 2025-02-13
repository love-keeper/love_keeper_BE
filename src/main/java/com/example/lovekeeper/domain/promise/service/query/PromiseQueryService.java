package com.example.lovekeeper.domain.promise.service.query;

import java.time.LocalDate;

import com.example.lovekeeper.domain.promise.dto.response.PromiseResponse;

public interface PromiseQueryService {

	/**
	 * 약속 리스트 조회
	 */
	PromiseResponse.PromiseListResponse getPromises(Long memberId, int page, int size);

	Long getPromiseCount(Long memberId);

	// 추가된 메서드 (특정 날짜 약속 조회)
	PromiseResponse.PromiseListResponse getPromisesByDate(Long memberId, LocalDate date, int page, int size);

}
