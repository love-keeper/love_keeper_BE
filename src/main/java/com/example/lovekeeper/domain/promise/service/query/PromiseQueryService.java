package com.example.lovekeeper.domain.promise.service.query;

import com.example.lovekeeper.domain.promise.dto.response.PromiseResponse;

public interface PromiseQueryService {

	/**
	 * 약속 리스트 조회
	 */
	PromiseResponse.PromiseListResponse getPromises(Long memberId, int page, int size);

	Long getPromiseCount(Long memberId);

}
