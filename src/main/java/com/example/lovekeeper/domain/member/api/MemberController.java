package com.example.lovekeeper.domain.member.api;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.auth.dto.request.SendEmailCodeRequest;
import com.example.lovekeeper.domain.auth.dto.response.SendCodeResponse;
import com.example.lovekeeper.domain.auth.service.command.EmailAuthCommandService;
import com.example.lovekeeper.domain.member.dto.request.ChangeBirthdayRequest;
import com.example.lovekeeper.domain.member.dto.request.ChangeEmailRequest;
import com.example.lovekeeper.domain.member.dto.request.ChangeNicknameRequest;
import com.example.lovekeeper.domain.member.dto.request.ChangePasswordRequest;
import com.example.lovekeeper.domain.member.dto.response.ChangeBirthdayResponse;
import com.example.lovekeeper.domain.member.dto.response.ChangeNicknameResponse;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.service.command.MemberCommandService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "회원", description = "회원 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberCommandService memberCommandService;
	private final EmailAuthCommandService emailAuthCommandService;

	/**
	 * 이메일 변경을 위한 인증 코드 발송
	 */
	@PostMapping("/email/send-code")
	public BaseResponse<SendCodeResponse> sendEmailChangeCode(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid SendEmailCodeRequest sendEmailCodeRequest) {

		// 사용자 조회
		if (userDetails == null) {
			throw new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND);
		}

		if (userDetails.getMember().getEmail().equals(sendEmailCodeRequest.getEmail())) {
			throw new MemberException(MemberErrorStatus.SAME_EMAIL);
		}

		// 소셜 멤버는 이메일 변경 불가
		if (userDetails.getMember().isSocialMember()) {
			throw new MemberException(MemberErrorStatus.SOCIAL_MEMBER_EMAIL_CANNOT_CHANGE);
		}

		return BaseResponse.onSuccess(emailAuthCommandService.sendVerificationCode(sendEmailCodeRequest.getEmail()));

	}

	/**
	 * 인증 코드 검증 후 이메일 변경
	 */
	@PatchMapping("/email/verify-code")
	public BaseResponse<String> changeEmail(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeEmailRequest changeEmailRequest) {
		// 인증 코드 검증 및 이메일 변경 로직 호출
		memberCommandService.changeEmailWithVerification(
			userDetails.getMember().getId(),
			changeEmailRequest.getEmail(),
			changeEmailRequest.getCode()); // 코드 추가 필요
		return BaseResponse.onSuccess("이메일 변경 성공");
	}

	/**
	 * 닉네임 변경
	 */
	@PatchMapping("/nickname")
	public BaseResponse<ChangeNicknameResponse> changeNickname(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeNicknameRequest changeNicknameRequest) {

		return BaseResponse.onSuccess(memberCommandService.changeNickname(userDetails.getMember().getId(),
			changeNicknameRequest.getNickname()));
	}

	/**
	 * 생일 변경
	 */
	@PatchMapping("/birthday")
	public BaseResponse<ChangeBirthdayResponse> changeBirthday(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeBirthdayRequest changeBirthdayRequest) {

		return BaseResponse.onSuccess(memberCommandService.changeBirthday(userDetails.getMember().getId(),
			changeBirthdayRequest.getBirthday()));
	}

	/**
	 * 비밀번호 변경
	 */
	@PatchMapping("/password")
	public BaseResponse<String> changePassword(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangePasswordRequest request
	) {

		memberCommandService.changePassword(userDetails.getMember().getId(), request);

		return BaseResponse.onSuccess("비밀번호 변경 성공");
	}

	@PatchMapping(value = "/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<String> updateProfileImage(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam MultipartFile profileImage) {
		memberCommandService.updateProfileImage(userDetails.getMember().getId(), profileImage);

		return BaseResponse.onSuccess("프로필 이미지가 변경되었습니다.");
	}

	@DeleteMapping
	public BaseResponse<String> withdrawMember(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		memberCommandService.withdrawMember(userDetails.getMember().getId());
		return BaseResponse.onSuccess("회원 탈퇴가 완료되었습니다.");
	}

}
