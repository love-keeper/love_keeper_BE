package com.example.lovekeeper.domain.calendar.service.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.calendar.dto.response.CalendarResponse;
import com.example.lovekeeper.domain.calendar.dto.response.DateCountResponse;
import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.letter.repository.LetterQueryRepository;
import com.example.lovekeeper.domain.promise.repository.PromiseQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarQueryServiceImpl implements CalendarQueryService {

	private final CoupleRepository coupleRepository;
	private final LetterQueryRepository letterQueryRepository;
	private final PromiseQueryRepository promiseQueryRepository;

	@Override
	public CalendarResponse getCalendarDataForMember(Long memberId, int year, int month, Integer day) {
		// 1. 현재 로그인한 사용자가 속한 커플 조회
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 2. 편지 및 약속 개수 조회
		List<DateCountResponse> letterDateCounts;
		List<DateCountResponse> promiseDateCounts;
		long totalLetterCount;
		long totalPromiseCount;
		long dailyLetterCount = 0;
		long dailyPromiseCount = 0;

		if (day != null) {
			// 특정 날짜에 대한 데이터 조회
			letterDateCounts = letterQueryRepository.findLetterCountByCoupleAndSpecificDate(couple.getId(), year, month,
				day);
			promiseDateCounts = promiseQueryRepository.findPromiseCountByCoupleAndSpecificDate(couple.getId(), year,
				month, day);
			// 특정 날짜의 총 개수 계산
			dailyLetterCount = letterQueryRepository.findTotalLetterCountByCoupleAndSpecificDate(couple.getId(), year,
				month, day);
			dailyPromiseCount = promiseQueryRepository.findTotalPromiseCountByCoupleAndSpecificDate(couple.getId(),
				year, month, day);
		} else {
			// 전체 월 데이터 조회
			letterDateCounts = letterQueryRepository.findLetterCountByCoupleAndYearMonth(couple.getId(), year, month);
			promiseDateCounts = promiseQueryRepository.findPromiseCountByCoupleAndYearMonth(couple.getId(), year,
				month);
		}

		// 3. 해당 월의 총 편지와 약속 개수 계산
		totalLetterCount = letterQueryRepository.findTotalLetterCountByCoupleAndYearMonth(couple.getId(), year, month);
		totalPromiseCount = promiseQueryRepository.findTotalPromiseCountByCoupleAndYearMonth(couple.getId(), year,
			month);

		// 4. 응답 DTO 생성 후 반환
		return CalendarResponse.of(letterDateCounts, promiseDateCounts, totalLetterCount, totalPromiseCount,
			dailyLetterCount, dailyPromiseCount);
	}
}