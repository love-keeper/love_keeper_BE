package com.example.lovekeeper.domain.member.service.command;

import org.springframework.web.multipart.MultipartFile;

public interface MemberCommandService {
	/**
	 * 회원 탈퇴
	 * @param memberId 탈퇴할 회원의 ID
	 */
	void withdrawMember(Long memberId);

	void updateProfileImage(Long memberId, MultipartFile profileImage);
}
