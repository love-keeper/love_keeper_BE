package com.example.lovekeeper.domain.couple.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.lovekeeper.domain.couple.dto.response.LetterCountResponse;
import com.example.lovekeeper.domain.couple.dto.response.PromiseCountResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CoupleQueryRepositoryImpl implements CoupleQueryRepository {

	/**
	 * 연도별, 월별 편지 수 조회
	 */
	@Override
	public List<LetterCountResponse> findLetterCountsByYearAndMonth(Long coupleId) {
		return List.of();
	}

	/**
	 * 연도별, 월별 약속 수 조회
	 */
	@Override
	public List<PromiseCountResponse> findPromiseCountsByYearAndMonth(Long coupleId) {
		return List.of();
	}
}
