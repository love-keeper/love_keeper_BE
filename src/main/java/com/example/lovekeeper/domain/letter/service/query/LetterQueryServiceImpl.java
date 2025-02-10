package com.example.lovekeeper.domain.letter.service.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
