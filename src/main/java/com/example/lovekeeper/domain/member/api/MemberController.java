package com.example.lovekeeper.domain.member.api;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.example.lovekeeper.domain.member.dto.response.MyInfoResponse;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.service.command.MemberCommandService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.infrastructure.service.s3.S3Service;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
	private final S3Service s3Service;

	@Operation(summary = "내 프로필 정보 조회",
		description = "인증된 사용자의 프로필 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로필 정보 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/me")
	public BaseResponse<MyInfoResponse> getMyInfo(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(memberCommandService.getMyInfo(userDetails.getMember().getId()));
	}

	@Operation(summary = "이메일 변경 인증 코드 발송",
		description = "새로운 이메일로 변경을 위한 인증 코드를 발송합니다. 소셜 로그인 사용자는 이메일 변경 불가.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "인증 코드 발송 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 이메일 형식 또는 기존 이메일과 동일",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "소셜 로그인 사용자는 이메일 변경 불가",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/email/send-code")
	public BaseResponse<SendCodeResponse> sendEmailChangeCode(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid SendEmailCodeRequest sendEmailCodeRequest) {
		if (userDetails == null) {
			throw new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND);
		}
		if (userDetails.getMember().getEmail().equals(sendEmailCodeRequest.getEmail())) {
			throw new MemberException(MemberErrorStatus.SAME_EMAIL);
		}
		if (userDetails.getMember().isSocialMember()) {
			throw new MemberException(MemberErrorStatus.SOCIAL_MEMBER_EMAIL_CANNOT_CHANGE);
		}
		return BaseResponse.onSuccess(emailAuthCommandService.sendVerificationCode(sendEmailCodeRequest.getEmail()));
	}

	@Operation(summary = "이메일 변경",
		description = "인증 코드를 검증하고 사용자의 이메일을 변경합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이메일 변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (이메일 형식, 코드 오류)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "유효하지 않은 인증 코드",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/email/verify-code")
	public BaseResponse<String> changeEmail(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeEmailRequest changeEmailRequest) {
		memberCommandService.changeEmailWithVerification(
			userDetails.getMember().getId(), changeEmailRequest.getEmail(), changeEmailRequest.getCode());
		return BaseResponse.onSuccess("이메일 변경 성공");
	}

	@Operation(summary = "닉네임 변경",
		description = "인증된 사용자의 닉네임을 변경합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "닉네임 변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 닉네임 형식 (길이 제한 등)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/nickname")
	public BaseResponse<ChangeNicknameResponse> changeNickname(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeNicknameRequest changeNicknameRequest) {
		return BaseResponse.onSuccess(memberCommandService.changeNickname(
			userDetails.getMember().getId(), changeNicknameRequest.getNickname()));
	}

	@Operation(summary = "생일 변경",
		description = "인증된 사용자의 생일을 변경합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "생일 변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 생일 형식 (미래 날짜 등)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/birthday")
	public BaseResponse<ChangeBirthdayResponse> changeBirthday(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeBirthdayRequest changeBirthdayRequest) {
		return BaseResponse.onSuccess(memberCommandService.changeBirthday(
			userDetails.getMember().getId(), changeBirthdayRequest.getBirthday()));
	}

	@Operation(summary = "비밀번호 변경",
		description = "인증된 사용자의 비밀번호를 변경합니다. 새 비밀번호와 확인이 일치해야 합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 비밀번호 형식 또는 현재 비밀번호 불일치",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/password")
	public BaseResponse<String> changePassword(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangePasswordRequest request) {
		memberCommandService.changePassword(userDetails.getMember().getId(), request);
		return BaseResponse.onSuccess("비밀번호 변경 성공");
	}

	@Operation(summary = "프로필 이미지 업로드 (Multipart)",
		description = "인증된 사용자의 프로필 이미지를 멀티파트 파일로 업로드합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로필 이미지가 변경되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 파일 형식",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping(value = "/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<String> updateProfileImage(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "업로드할 프로필 이미지 파일", required = true)
		@RequestParam MultipartFile profileImage) {
		log.info("[MultipartFile Upload] Start - memberId: {}", userDetails.getMember().getId());
		long startTime = System.currentTimeMillis();
		memberCommandService.updateProfileImage(userDetails.getMember().getId(), profileImage);
		long endTime = System.currentTimeMillis();
		log.info("[MultipartFile Upload] End - memberId: {}, Duration: {}ms",
			userDetails.getMember().getId(), endTime - startTime);
		return BaseResponse.onSuccess("프로필 이미지가 변경되었습니다.");
	}

	@Operation(summary = "프로필 이미지 Presigned URL 생성",
		description = "S3에 업로드할 프로필 이미지의 Presigned URL을 생성합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "파일 이름이 비어 있음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/profileImage/presigned-url")
	public BaseResponse<String> getProfileImagePresignedUrl(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "업로드할 파일 이름", required = true, example = "profile.jpg")
		@RequestParam @NotBlank String fileName) {
		log.info("[Presigned URL] Generation Start - memberId: {}, fileName: {}",
			userDetails.getMember().getId(), fileName);
		long startTime = System.currentTimeMillis();
		String presignedUrl = s3Service.generatePresignedUrl(fileName);
		long endTime = System.currentTimeMillis();
		log.info("[Presigned URL] Generation End - memberId: {}, Duration: {}ms",
			userDetails.getMember().getId(), endTime - startTime);
		return BaseResponse.onSuccess(presignedUrl);
	}

	@Operation(summary = "프로필 이미지 URL 업데이트",
		description = "S3에 업로드된 이미지 URL로 프로필 이미지를 업데이트합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로필 이미지가 업데이트되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 이미지 URL",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/profileImage/update-url")
	public BaseResponse<String> updateProfileImageUrl(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "S3에 업로드된 이미지 URL", required = true,
			example = "https://s3.amazonaws.com/bucket/profile.jpg")
		@RequestParam @NotBlank String imageUrl) {
		log.info("[Presigned URL] Update Start - memberId: {}, imageUrl: {}",
			userDetails.getMember().getId(), imageUrl);
		long startTime = System.currentTimeMillis();
		memberCommandService.updateProfileImage(userDetails.getMember().getId(), imageUrl);
		long endTime = System.currentTimeMillis();
		log.info("[Presigned URL] Update End - memberId: {}, Duration: {}ms",
			userDetails.getMember().getId(), endTime - startTime);
		return BaseResponse.onSuccess("프로필 이미지가 업데이트되었습니다.");
	}

	@Operation(summary = "회원 탈퇴",
		description = "인증된 사용자를 탈퇴 처리합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 탈퇴가 완료되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@DeleteMapping
	public BaseResponse<String> withdrawMember(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		memberCommandService.withdrawMember(userDetails.getMember().getId());
		return BaseResponse.onSuccess("회원 탈퇴가 완료되었습니다.");
	}
}