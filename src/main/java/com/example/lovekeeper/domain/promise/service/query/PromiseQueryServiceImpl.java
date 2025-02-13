package com.example.lovekeeper.domain.promise.service.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.promise.dto.response.PromiseResponse;
import com.example.lovekeeper.domain.promise.model.Promise;
import com.example.lovekeeper.domain.promise.repository.PromiseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromiseQueryServiceImpl implements PromiseQueryService {

	private final PromiseRepository promiseRepository;
	private final CoupleRepository coupleRepository;

	@Override
	public PromiseResponse.PromiseListResponse getPromises(Long memberId, int page, int size) {
		// PageRequest에 정렬 조건을 추가할 수도 있음 (예: 약속 날짜 기준 역순)
		Pageable pageable = PageRequest.of(page, size, Sort.by("promisedAt").descending());

		// 커플 찾기
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		Slice<Promise> promises = promiseRepository.findByCoupleId(couple.getId(), pageable);

		return PromiseResponse.PromiseListResponse.from(promises);
	}
}
