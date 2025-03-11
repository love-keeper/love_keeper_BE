package com.example.lovekeeper.domain.promise.service.command;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import com.example.lovekeeper.global.infrastructure.service.fcm.FCMService;

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

	private final FCMService fcmService;

	@Override
	public void createPromise(Long memberId, String content) {
		// 현재 멤버 찾기
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 커플 찾기
		Couple currentCouple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		log.info("약속을 생성합니다.");

		LocalDate promisedAt = LocalDate.now();
		LocalDateTime createdAtEstimate = LocalDateTime.now();
		log.info("Creating promise - promisedAt: {}, estimated createdAt: {}, timezone: {}",
			promisedAt, createdAtEstimate, java.time.ZoneId.systemDefault());
		
		// 약속 생성
		promiseRepository.save(Promise.createPromise(currentCouple, currentMember, content));

		// 파트너 정하기
		Member partner = currentCouple.getMember1() == currentMember
			? currentCouple.getMember2()
			: currentCouple.getMember1();

		// 약속 생성 푸시 알림 전송
		fcmService.sendPushNotification(
			partner.getId(),
			String.valueOf(currentCouple.getPromiseCount()) + " 번째 약속이 정해졌어요.",
			"새로운 약속이 등록되었어요",
			System.currentTimeMillis()
		);

	}

	@Override
	public void deletePromise(Long memberId, Long promiseId) {
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 약속 찾기
		Promise promise = promiseRepository.findByIdAndCoupleId(promiseId, couple.getId())
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 삭제
		promiseRepository.delete(promise);
	}
}
