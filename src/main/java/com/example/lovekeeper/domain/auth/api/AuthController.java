package com.example.lovekeeper.domain.auth.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.auth.dto.request.ChangePasswordAfterResetRequest;
import com.example.lovekeeper.domain.auth.dto.request.EmailDuplicationRequest;
import com.example.lovekeeper.domain.auth.dto.request.ResetPasswordRequest;
import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.ReissueResponse;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;
import com.example.lovekeeper.domain.auth.service.command.AuthCommandService;
import com.example.lovekeeper.domain.auth.service.command.EmailAuthCommandService;
import com.example.lovekeeper.domain.auth.service.query.AuthQueryService;
import com.example.lovekeeper.domain.member.exception.annotation.UniqueEmail;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.infrastructure.service.refreshredis.RefreshTokenRedisService;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃 등 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthCommandService authCommandService;
	private final AuthQueryService authQueryService;
	private final EmailAuthCommandService emailAuthcommandservice;
	private final RefreshTokenRedisService refreshTokenRedisService;

	/**
	 * 이메일 중복 확인
	 */
	@Operation(summary = "이메일 중복 확인", description = "주어진 이메일이 이미 사용 중인지 확인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (이메일 형식이 올바르지 않음)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/email-duplication")
	public BaseResponse<String> checkEmailDuplication(
		@RequestBody @Valid EmailDuplicationRequest emailDuplicationRequest) {
		authQueryService.checkEmailDuplication(emailDuplicationRequest.getEmail());
		return BaseResponse.onSuccess("사용 가능한 이메일입니다.");
	}

	/**
	 * 회원가입
	 */
	@Operation(summary = "회원가입", description = "새로운 회원을 등록합니다. 로컬 회원가입 시 비밀번호와 닉네임은 필수이며, 소셜 로그인 시 provider와 providerId가 필요합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원가입 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (이메일, 비밀번호 형식 등)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<SignUpResponse> signUp(
		@Parameter(description = "사용자 이메일", required = true, example = "user@example.com")
		@RequestParam @Email @UniqueEmail String email,
		@Parameter(description = "비밀번호 (로컬 회원가입 시 필수, 8~20자 영문/숫자/특수문자 포함)", required = false)
		@RequestParam(required = false) @Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
			message = "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다.")
		String password,
		@Parameter(description = "닉네임 (2~10자)", required = true, example = "lover")
		@RequestParam @Size(min = 1, max = 10) String nickname,
		@Parameter(description = "생년월일 (yyyy-MM-dd 형식)", required = true, example = "1990-01-01")
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
		@Parameter(description = "프로필 이미지 파일 (선택 사항)", required = false)
		@RequestParam(required = false) MultipartFile profileImage,
		@Parameter(description = "인증 제공자 (LOCAL, GOOGLE, KAKAO 등)", required = true, example = "LOCAL")
		@RequestParam Provider provider,
		@Parameter(description = "소셜 로그인 제공자 ID (소셜 로그인 시 필수)", required = false, example = "123456789")
		@RequestParam(required = false) String providerId,
		@Parameter(description = "개인정보 처리 방침 동의 여부", required = true, example = "true")
		@RequestParam boolean privacyPolicyAgreed,
		@Parameter(description = "마케팅 정보 수신 동의 여부", required = true, example = "false")
		@RequestParam boolean marketingAgreed,
		@Parameter(description = "서비스 이용 약관 동의 여부", required = true, example = "true")
		@RequestParam boolean termsOfServiceAgreed) {
		return BaseResponse.onSuccessCreate(authCommandService.signUpMember(
			SignUpRequest.of(email, password, nickname, birthDate, profileImage, provider, providerId,
				privacyPolicyAgreed, marketingAgreed, termsOfServiceAgreed)));
	}

	/**
	 * 비밀번호 초기화 요청
	 */
	@Operation(summary = "비밀번호 초기화 요청", description = "사용자의 이메일로 비밀번호 변경 링크를 발송합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 링크가 이메일로 발송되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 이메일 형식",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "등록되지 않은 이메일",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/password/reset-request")
	public BaseResponse<String> resetPasswordRequest(@RequestBody @Valid ResetPasswordRequest request) {
		emailAuthcommandservice.sendPasswordChangeLink(request.getEmail());
		return BaseResponse.onSuccess("비밀번호 변경 링크가 이메일로 발송되었습니다.");
	}

	/**
	 * 비밀번호 초기화 후 변경
	 */
	@Operation(summary = "비밀번호 초기화 후 변경", description = "비밀번호 초기화 링크를 통해 새로운 비밀번호로 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (비밀번호 형식 등)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "유효하지 않은 인증 코드",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PatchMapping("/password/reset")
	public BaseResponse<String> resetPassword(
		@RequestBody @Valid ChangePasswordAfterResetRequest request) {
		authCommandService.resetPassword(request);
		return BaseResponse.onSuccess("비밀번호 변경 성공");
	}

	/**
	 * 토큰 재발급
	 */
	@Operation(summary = "토큰 재발급", description = "쿠키에 담긴 Refresh Token을 이용해 새로운 Access Token과 Refresh Token을 발급받습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "Refresh Token이 쿠키에 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/reissue")
	public BaseResponse<String> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
		String oldRefreshToken = extractRefreshTokenFromCookie(request);
		if (oldRefreshToken == null) {
			return BaseResponse.onFailure("AUTH003", "Refresh Token이 쿠키에 존재하지 않습니다.", null);
		}
		ReissueResponse reissueResponse = authCommandService.reissueRefreshToken(oldRefreshToken);
		response.setHeader("Authorization", "Bearer " + reissueResponse.getAccessToken());
		Cookie refreshCookie = new Cookie("refresh_token", reissueResponse.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60);
		response.addCookie(refreshCookie);
		log.info("[AuthController] 토큰 재발급 - Access/Refresh Token 생성 완료");
		return BaseResponse.onSuccessCreate("토큰 재발급 성공");
	}

	/**
	 * 토큰 유효성 확인
	 */
	@Operation(summary = "토큰 유효성 확인", description = "현재 사용 중인 Access Token의 유효성을 확인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰이 유효합니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "토큰이 유효하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/check-token")
	public BaseResponse<String> checkToken(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		if (userDetails == null) {
			return BaseResponse.onFailure("COMMON401", "토큰이 유효하지 않습니다.", null);
		}
		return BaseResponse.onSuccess("토큰이 유효합니다.");
	}

	/**
	 * 로그아웃
	 */
	@Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃 처리하고, Refresh Token을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/logout")
	public BaseResponse<String> logout(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		HttpServletResponse response) {
		refreshTokenRedisService.deleteRefreshToken(userDetails.getMember().getId());
		Cookie refreshCookie = new Cookie("refresh_token", null);
		refreshCookie.setMaxAge(0);
		refreshCookie.setPath("/");
		response.addCookie(refreshCookie);
		SecurityContextHolder.clearContext();
		return BaseResponse.onSuccess("로그아웃 되었습니다.");
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if ("refresh_token".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}