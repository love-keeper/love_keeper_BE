package com.example.lovekeeper.domain.member.service.command;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.member.dto.request.ChangePasswordRequest;
import com.example.lovekeeper.domain.member.dto.response.ChangeBirthdayResponse;
import com.example.lovekeeper.domain.member.dto.response.ChangeNicknameResponse;
import com.example.lovekeeper.domain.member.dto.response.MyInfoResponse;
import com.example.lovekeeper.domain.member.model.MemberStatus;

public interface MemberCommandService {

	/**
	 * 내 정보 가져오기
	 */
	MyInfoResponse getMyInfo(Long memberId);

	/**
	 * 비밀번호 변경
	 */
	void changePassword(Long memberId, ChangePasswordRequest request);

	/**
	 * 회원 탈퇴
	 * @param memberId 탈퇴할 회원의 ID
	 */
	void withdrawMember(Long memberId);

	String getProfileImagePresignedUrl(String fileName);

	void saveProfileImageUrl(Long memberId, String imageUrl);

	void updateProfileImage(Long memberId, MultipartFile profileImage);

	void updateProfileImage(Long memberId, String imageUrl);

	ChangeNicknameResponse changeNickname(Long memberId, String nickname);

	ChangeBirthdayResponse changeBirthday(Long memberId, LocalDate birthday);

	void changeEmailWithVerification(Long memberId, String email, String code);

	void updateMemberStatus(Long memberId, MemberStatus status);
}
