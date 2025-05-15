package com.example.lovekeeper.domain.couple.repository;

import java.util.List;

import com.example.lovekeeper.domain.couple.dto.response.LetterCountResponse;
import com.example.lovekeeper.domain.couple.dto.response.PromiseCountResponse;

public interface CoupleQueryRepository {
	List<LetterCountResponse> findLetterCountsByYearAndMonth(Long coupleId);

	List<PromiseCountResponse> findPromiseCountsByYearAndMonth(Long coupleId);
}
