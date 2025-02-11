package com.example.lovekeeper.domain.couple.service.command;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory;
import com.example.lovekeeper.domain.connectionhistory.repository.ConnectionHistoryRepository;
import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleCommandServiceImpl implements CoupleCommandService {

	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;
	private final ConnectionHistoryRepository connectionHistoryRepository;

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
	public void connectCouple(Long currentMemberId, String inviteCode) {
		// 초대 코드를 입력하는 본인
		Member currentMember = memberRepository.findById(currentMemberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 초대 코드를 소유한 상대방
		Member partnerMember = memberRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.INVITE_CODE_NOT_FOUND));

		Couple newCouple = Couple.connectCouple(currentMember, partnerMember);

		ConnectionHistory connectionHistory
			= ConnectionHistory.makeHistory(currentMember, partnerMember, newCouple);

		// 커플 저장
		coupleRepository.save(newCouple);
		connectionHistoryRepository.save(connectionHistory);

		// 초대 코드 초기화
		currentMember.updateInviteCode(null);
		partnerMember.updateInviteCode(null); // 상대방도 초기화

	}

	/**
	 * 커플의 시작일 수정
	 */
	@Override
	public void updateCoupleStartDate(Long memberId, LocalDate newStartDate) {
		// 현재 회원 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

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
