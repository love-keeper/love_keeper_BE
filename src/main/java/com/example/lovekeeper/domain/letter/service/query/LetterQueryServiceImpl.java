package com.example.lovekeeper.domain.letter.service.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;
import com.example.lovekeeper.domain.letter.exception.LetterErrorStatus;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.letter.repository.LetterRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LetterQueryServiceImpl implements LetterQueryService {

	private final LetterRepository letterRepository;
	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;

	/**
	 * 편지 리스트 조회
	 */
	@Override
	public LetterResponse.LetterListResponse getLetters(Long memberId, int page, int size) {

		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// Pageable 객체 생성 (page, size)
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentDate").descending());

		// Slice 객체로 데이터를 조회 (total count 없이, 다음 페이지 존재 여부만 체크)
		Slice<Letter> letterSlice = letterRepository.findBySenderOrReceiver(member, member, pageRequest);

		// LetterListResponse의 정적 팩토리 메서드 사용하여 변환
		return LetterResponse.LetterListResponse.from(letterSlice);
	}

	@Override
	public Long getLetterCount(Long memberId) {

		// 현재 커플 찾기
		Couple currentCouple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 현재 커플이 주고받은 편지의 개수 조회
		return letterRepository.countByCoupleId(currentCouple.getId());

	}

	@Override
	public LetterResponse.LetterDetailResponse getLetterDetail(Long memberId, Long letterId) {
		// 편지 조회
		Letter letter = letterRepository.findById(letterId)
			.orElseThrow(() -> new MemberException(LetterErrorStatus.LETTER_NOT_FOUND)); // 새로운 예외 필요 시 정의

		// 권한 확인: 현재 사용자가 편지의 발신자 또는 수신자인지 확인
		if (!letter.getSender().getId().equals(memberId) && !letter.getReceiver().getId().equals(memberId)) {
			throw new MemberException(LetterErrorStatus.NO_PERMISSION_TO_ACCESS_LETTER); // 새로운 예외 필요 시 정의
		}

		return LetterResponse.LetterDetailResponse.from(letter);
	}

	@Override
	public LetterResponse.LetterListResponse getLettersByDate(Long memberId, String date, int page, int size) {
		LocalDateTime dt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		Pageable pageable = PageRequest.of(page, size, Sort.by("sentDate", "createdAt").descending());
		// 커플 정보 조회
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));
		// 특정 날짜 편지 조회


		Slice<Letter> letters = letterRepository.findByCoupleIdAndSentDate(couple.getId(), dt, pageable);
		return LetterResponse.LetterListResponse.from(letters);
	}
}
