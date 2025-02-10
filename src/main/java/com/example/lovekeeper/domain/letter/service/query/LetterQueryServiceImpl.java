package com.example.lovekeeper.domain.letter.service.query;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse;
import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.letter.repository.LetterJpaRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LetterQueryServiceImpl implements LetterQueryService {

	private final LetterJpaRepository letterJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 편지 리스트 조회
	 */
	@Override
	public LetterResponse.LetterListResponse getLetters(Long memberId, int page, int size) {

		// 멤버 조회
		Member member = memberJpaRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// Pageable 객체 생성 (page, size)
		PageRequest pageRequest = PageRequest.of(page, size);

		// Slice 객체로 데이터를 조회 (total count 없이, 다음 페이지 존재 여부만 체크)
		Slice<Letter> letterSlice = letterJpaRepository.findBySenderOrReceiver(member, member, pageRequest);

		// LetterListResponse의 정적 팩토리 메서드 사용하여 변환
		return LetterResponse.LetterListResponse.from(letterSlice);
	}

	/**
	 * 연도와 월을 기준으로 각 날짜별 편지 수 합산하여 반환
	 */
	@Override
	public List<LetterCountByDateResponse> getLetterCountByMonth(int year, Month month, Long memberId) {
		// 월의 첫 번째 날과 마지막 날을 구합니다.
		LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
		LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

		// 받은 편지 수와 보낸 편지 수를 각각 조회합니다.
		List<LetterCountByDateResponse> receivedLetters = letterJpaRepository.countReceivedLettersByDate(
			firstDayOfMonth, lastDayOfMonth, memberId);
		List<LetterCountByDateResponse> sentLetters = letterJpaRepository.countSentLettersByDate(
			firstDayOfMonth, lastDayOfMonth, memberId);

		log.info("Received letters: {}", receivedLetters);
		log.info("Sent letters: {}", sentLetters);

		// 해당 월의 모든 날짜를 포함하는 Map 생성
		Map<LocalDate, LetterCountByDateResponse> letterCountMap = new HashMap<>();

		// 월의 모든 날짜에 대해 빈 응답 객체 생성
		for (int day = 1; day <= lastDayOfMonth.getDayOfMonth(); day++) {
			LocalDate date = LocalDate.of(year, month, day);
			letterCountMap.put(date, LetterCountByDateResponse.builder()
				.date(date)
				.receivedCount(0)
				.sentCount(0)
				.totalCount(0)
				.build());
		}

		// 받은 편지 수 집계
		for (LetterCountByDateResponse received : receivedLetters) {
			LocalDate date = received.getDate();
			int receivedCount = received.getReceivedCount();

			LetterCountByDateResponse existing = letterCountMap.get(date);
			letterCountMap.put(date, LetterCountByDateResponse.builder()
				.date(date)
				.receivedCount(receivedCount)
				.sentCount(existing.getSentCount())
				.totalCount(receivedCount + existing.getSentCount())
				.build());
		}

		// 보낸 편지 수 집계
		for (LetterCountByDateResponse sent : sentLetters) {
			LocalDate date = sent.getDate();
			int sentCount = sent.getReceivedCount(); // Query 결과가 receivedCount에 들어있음

			LetterCountByDateResponse existing = letterCountMap.get(date);
			letterCountMap.put(date, LetterCountByDateResponse.builder()
				.date(date)
				.receivedCount(existing.getReceivedCount())
				.sentCount(sentCount)
				.totalCount(existing.getReceivedCount() + sentCount)
				.build());
		}

		// Map을 List로 변환하고 날짜순으로 정렬
		return letterCountMap.values().stream()
			.sorted(Comparator.comparing(LetterCountByDateResponse::getDate))
			.collect(Collectors.toList());
	}
}
