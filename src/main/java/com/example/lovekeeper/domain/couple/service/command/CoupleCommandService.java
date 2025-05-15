package com.example.lovekeeper.domain.couple.service.command;

import java.time.LocalDate;

import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;

public interface CoupleCommandService {

	/**
	 * 초대 코드 생성 및 저장
	 */
	GenerateCodeResponse generateInviteCode(Long memberId);

	/**
	 * 초대 코드를 이용해 두 회원 커플 연결
	 */
	void connectCouple(Long currentMemberId, String inviteCode);

	/**
	 * 커플의 시작일 수정
	 * @param memberId 회원 ID
	 * @param newStartDate 새로운 시작일
	 */
	void updateCoupleStartDate(Long memberId, LocalDate newStartDate);

	void disconnectCouple(Long memberId);

}
