package com.example.lovekeeper.domain.couple.service.command;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleJpaRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Status;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleCommandServiceImpl implements CoupleCommandService {

	private final MemberJpaRepository memberJpaRepository;
	private final CoupleJpaRepository coupleJpaRepository;

	/**
	 * 초대 코드 생성 및 저장
	 */
	public GenerateCodeResponse generateInviteCode(Long memberId) {
		Member member = memberJpaRepository.findById(memberId)
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
	public void connectCouple(Long currentMemberId, String inviteCode) {
		// 초대 코드를 입력하는 본인
		Member currentMember = memberJpaRepository.findById(currentMemberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 초대 코드를 소유한 상대방
		Member partnerMember = memberJpaRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.INVITE_CODE_NOT_FOUND));

		// 이미 커플인 경우 예외 처리할 수도 있음
		if (currentMember.getCouple() != null) {
			throw new MemberException(MemberErrorStatus.ALREADY_HAS_PARTNER);
		}
		if (partnerMember.getCouple() != null) {
			throw new MemberException(MemberErrorStatus.ALREADY_HAS_PARTNER);
		}

		// 커플 엔티티 생성
		Couple couple = Couple.builder()
			.startedAt(LocalDate.now())
			.status(Status.ACTIVE)
			.build();
		coupleJpaRepository.save(couple);

		// 양쪽 member에 couple 설정
		currentMember.updateCouple(couple);
		partnerMember.updateCouple(couple);

		// 서로를 partner로 설정
		currentMember.updatePartner(partnerMember);
		partnerMember.updatePartner(currentMember);
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
