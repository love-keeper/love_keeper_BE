package com.example.lovekeeper.domain.member.service.command;

public interface MemberCommandService {
	/**
	 * 회원 탈퇴
	 * @param memberId 탈퇴할 회원의 ID
	 */
	void withdrawMember(Long memberId);
}
