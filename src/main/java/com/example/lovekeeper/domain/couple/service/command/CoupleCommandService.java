package com.example.lovekeeper.domain.couple.service.command;

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
}
