package com.example.lovekeeper.domain.promise.service.query;

import java.time.LocalDate;

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

	@Override
	public Long getPromiseCount(Long memberId) {

		// 현재 커플 찾기
		Couple currentCouple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 현재 커플 약속 전체 개수를 조회
		return promiseRepository.countByCoupleId(currentCouple.getId());

	}

	@Override
	public PromiseResponse.PromiseListResponse getPromisesByDate(Long memberId, LocalDate date, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("promisedAt", "createdAt").descending());
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		Slice<Promise> promises = promiseRepository.findByCoupleIdAndPromisedAt(couple.getId(), date, pageable);
		return PromiseResponse.PromiseListResponse.from(promises);
	}
}
