package com.example.lovekeeper.domain.couple.service.query;

import com.example.lovekeeper.domain.couple.dto.response.CountResponse;

public interface CoupleQueryService {

	/**
	 * 커플이 사귄 지 며칠이 지났는지 계산
	 * @param memberId 멤버 ID
	 * @return 커플이 사귄 지 경과한 날짜 수
	 */
	long getDaysSinceStarted(Long memberId);

	/**
	 * 커플의 Promise와 Letter의 개수를 조회
	 */
	CountResponse getLettersAndPromisesCount(Long memberId);
}


