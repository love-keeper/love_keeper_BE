package com.example.lovekeeper.domain.promise.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.example.lovekeeper.domain.promise.model.Promise;
import com.example.lovekeeper.domain.promise.repository.PromiseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromiseCommandServiceImpl implements PromiseCommandService {

	private final PromiseRepository promiseRepository;
	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;

	@Override
	public void createPromise(Long memberId, String content) {
		// 현재 멤버 찾기
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 커플 찾기
		Couple currentCouple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 약속 생성

		log.info("약속을 생성합니다.");
		
		promiseRepository.save(Promise.createPromise(currentCouple, currentMember, content));

	}
}
