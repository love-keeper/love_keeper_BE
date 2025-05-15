package com.example.lovekeeper.domain.couple.service.command;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CoupleCommandServiceImpl implements CoupleCommandService {

	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;

	/**
	 * 초대 코드 생성 및 저장
	 */
	public GenerateCodeResponse generateInviteCode(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 임의의 8자리 영문+숫자
		String code = generateRandomCode(8);
		member.updateInviteCode(code);

		// JPA 더티 체킹으로 업데이트 자동 반영
		return GenerateCodeResponse.of(code);
	}

	/**
	 * 초대 코드를 이용해 두 회원 커플 연결
	 */
	@Override
	public void connectCouple(Long currentMemberId, String inviteCode) {
		// 1. 현재 회원 조회
		Member currentMember = memberRepository.findById(currentMemberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2. 초대 코드로 상대방 회원 조회
		Member partnerMember = memberRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.INVITE_CODE_NOT_FOUND));

		// 3. 자신의 초대 코드로 연결 시도하는지 확인
		if (currentMember.equals(partnerMember)) {
			throw new MemberException(MemberErrorStatus.SELF_INVITE_CODE);
		}

		// 4. 둘 다 현재 활성화된 커플 관계가 없는지 확인
		if (currentMember.getActiveCouple().isPresent()) {
			throw new CoupleException(CoupleErrorStatus.ALREADY_COUPLE);
		}
		if (partnerMember.getActiveCouple().isPresent()) {
			throw new CoupleException(CoupleErrorStatus.ALREADY_COUPLE);
		}

		// 5. 이전 커플 관계가 있는지 확인
		Optional<Couple> previousCouple = currentMember.findPreviousCoupleWith(partnerMember);

		Couple couple;
		if (previousCouple.isPresent()) {
			// 5-1. 이전 커플 관계가 있으면 재연결
			couple = previousCouple.get();
			couple.reconnect();
			log.info("커플 재연결 완료 - coupleId: {}, member1Id: {}, member2Id: {}",
				couple.getId(), currentMemberId, partnerMember.getId());
		} else {
			// 5-2. 이전 커플 관계가 없으면 새로 생성
			couple = Couple.connectCouple(currentMember, partnerMember);
			coupleRepository.save(couple);
			log.info("새로운 커플 연결 완료 - member1Id: {}, member2Id: {}",
				currentMemberId, partnerMember.getId());
		}

		// 6. 양쪽 회원의 상태 업데이트
		currentMember.updateCoupleStatus(CoupleStatus.CONNECTED);
		partnerMember.updateCoupleStatus(CoupleStatus.CONNECTED);

		// 7. 초대 코드 초기화
		currentMember.updateInviteCode(null);
		partnerMember.updateInviteCode(null);
	}

	/**
	 * 커플의 시작일 수정
	 */
	@Override
	public void updateCoupleStartDate(Long memberId, LocalDate newStartDate) {
		// 현재 회원 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 커플 조회
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 커플 시작일 수정
		couple.updateStartDate(newStartDate);

	}

	@Override
	public void disconnectCouple(Long memberId) {
		// 1. 회원 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2. 현재 활성화된 커플 관계 조회
		Couple activeCouple = member.getActiveCouple()
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 3. 커플 연결 상태 변경
		activeCouple.disconnect();

		// 4. 양쪽 회원의 상태 업데이트
		Member partner = activeCouple.getPartner(member);
		member.updateCoupleStatus(CoupleStatus.DISCONNECTED);
		partner.updateCoupleStatus(CoupleStatus.DISCONNECTED);

		// 5. 커플 연결 해제 시간 설정
		activeCouple.setEndedAt(LocalDate.now());

		log.info("커플 연결 해제 완료 - coupleId: {}, member1Id: {}, member2Id: {}",
			activeCouple.getId(), member.getId(), partner.getId());
	}

	private void validateNoCurrentCouple(Long memberId) {
		coupleRepository.findByMemberId(memberId)
			.filter(couple -> couple.getStatus() == CoupleStatus.CONNECTED)
			.ifPresent(couple -> {
				throw new CoupleException(CoupleErrorStatus.ALREADY_COUPLE);
			});
	}

	/**
	 * 랜덤 코드 생성 예시
	 */
	private String generateRandomCode(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}
}
