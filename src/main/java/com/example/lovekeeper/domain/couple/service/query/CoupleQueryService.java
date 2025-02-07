package com.example.lovekeeper.domain.couple.service.query;

public interface CoupleQueryService {

	/**
	 * 커플이 사귄 지 며칠이 지났는지 계산
	 * @param memberId 멤버 ID
	 * @return 커플이 사귄 지 경과한 날짜 수
	 */
	long getDaysSinceStarted(Long memberId);

}


